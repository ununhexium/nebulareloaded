package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.ImageCoordinates
import net.lab0.nebula.reloaded.image.MandelbrotRenderer
import net.lab0.nebula.reloaded.image.PlanViewport
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.image.withAlpha
import net.lab0.nebula.reloaded.mandelbrot.ComputeEngine
import net.lab0.nebula.reloaded.mandelbrot.Engines
import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.nebula.reloaded.tree.TreeNode
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JLabel
import javax.swing.JPanel

class MandelbrotPanel : JPanel() {
  var viewport = createDefaultViewport()
    private set

  private var computeEngine: ComputeEngine = Engines.MaxParallelStreamOptim2
  private var tree = TreeNode(
      -2 to 2,
      -2 to 2,
      MetaData(512, 8, Engines.Default)
  )

  private var iterationLimit = 512L

  lateinit var realValueLabel: JLabel
  lateinit var imgValueLabel: JLabel
  lateinit var xValueLabel: JLabel
  lateinit var yValueLabel: JLabel

  private var drawFractal = true
  private var drawTree = true

  private var selectionBox: Pair<MouseEvent, MouseEvent>? = null


  private val lastRenderingRef = AtomicReference<BufferedImage>()
  /**
   * Event store to tell that an image update event has been received.
   */
  private val blockingQueue = ArrayBlockingQueue<Any>(1)
  /**
   * Holder for a single thread: the one is charge of checking that an image update request has been received.
   */
  private val imageUpdateWatcher = Executors.newSingleThreadExecutor()

  init {
    asyncUpdateMandelbrotRendering()
    imageUpdateWatcher.execute(ImageUpdateWatcher(this, blockingQueue))
    /*
     * We only want to notify.
     * If a value was already present, then notifying again will not change anything.
     */
    blockingQueue.offer(TOKEN) // NOSONAR
  }

  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val g = graphics as Graphics2D
    synchronized(lastRenderingRef) {
      if (drawFractal && lastRenderingRef.get() != null) {
        g.drawRenderedImage(lastRenderingRef.get(), AffineTransform())
      }
    }

    val tooSmallRatio = 16

    fun TreeNode.isTooSmall() =
        this.position.width < viewport.width / tooSmallRatio &&
            this.position.height < viewport.height / tooSmallRatio

    if (drawTree) {
      val toRender = tree.getNodesBreadthFirst(
          depthFilter = { !it.isTooSmall() },
          filter = {
            it.position.overlaps(viewport) &&
                (!it.hasChildren() || it.isTooSmall())
          }
      )
      log.debug("Found ${toRender.size} nodes to render")
      renderAreas(g, toRender)
    }
  }

  private fun renderAreas(
      g: Graphics2D,
      nodes: List<TreeNode>
  ) {
    val rasterizationContext = RasterizationContext(
        viewport,
        this.width,
        this.height
    )
    nodes.forEach { node ->
      val color = when (node.payload.status) {
        UNDEFINED -> Color(255, 255, 255)
        OUTSIDE -> Color(0, 150, 200)
        INSIDE -> Color(200, 150, 0)
        EDGE -> Color(0, 128, 0)
      }

      val topLeft = rasterizationContext.convert(node.position.topLeft)
      val bottomRight = rasterizationContext.convert(node.position.bottomRight)
      val width = bottomRight.x - topLeft.x
      val height = bottomRight.y - topLeft.y

      paintAsRegular(g, color, topLeft.x, topLeft.y, width, height)
    }
  }

  private fun paintAsRegular(
      g: Graphics2D,
      color: Color,
      x: Int,
      y: Int,
      width: Int,
      height: Int
  ) {
    g.paint = color.withAlpha(0.25)
    g.fillRect(x + 1, y + 1, width - 2, height - 2)
    g.paint = color.withAlpha(0.5)
    g.drawRect(x, y, width - 1, height - 1)
  }

  /**
   * Adds a flag to tell that the image has to be updated.
   */
  fun asyncUpdateMandelbrotRendering() {
    if (drawFractal) {
      blockingQueue.offer(TOKEN)
    }
  }

  internal fun updateMandelbrotRendering() {
    if (this.width == 0 || this.height == 0) {
      // skip because can't create an image
      return
    }

    val renderer = MandelbrotRenderer(viewport)
    val image = renderer.render(
        this.width,
        this.height,
        iterationLimit,
        computeEngine
    )
    synchronized(lastRenderingRef) {
      lastRenderingRef.set(image)
    }
  }

  fun setSelectionBox(startToEnd: Pair<MouseEvent, MouseEvent>?) {
    this.selectionBox = startToEnd
  }

  fun moveImage(movement: Pair<MouseEvent, MouseEvent>) {
    val context = RasterizationContext(viewport, width, height)
    val from = ImageCoordinates(movement.first.x, movement.first.y)
    val to = ImageCoordinates(movement.second.x, movement.second.y)

    val oldPosition = context.convert(from)
    val newPosition = context.convert(to)
    viewport = viewport.translate(oldPosition.minus(newPosition))
    asyncUpdateMandelbrotRendering()
  }

  fun resetViewport() {
    viewport = createDefaultViewport()
    asyncUpdateMandelbrotRendering()
  }

  private fun createDefaultViewport(): PlanViewport {
    return PlanViewport(
        Pair(-2, 2), Pair(-2, 2)
    )
  }

  fun zoom(factor: Double) {
    viewport = viewport.zoom(factor)
    asyncUpdateMandelbrotRendering()
  }

  fun setComputeEngine(computeEngine: ComputeEngine) {
    log.debug("Switching compute engine to $computeEngine")
    object : Thread() {
      override fun run() {
        this@MandelbrotPanel.computeEngine = computeEngine
      }
    }.start()
  }

  fun setShowFractal(enabled: Boolean) {
    this.drawFractal = enabled
    if (this.drawFractal) {
      asyncUpdateMandelbrotRendering()
    }
    else {
      EventQueue.invokeLater {
        this.repaint()
      }
    }
  }

  fun setShowTree(selected: Boolean) {
    this.drawTree = selected
    if (selected) {
      asyncUpdateMandelbrotRendering()
    }
    else {
      EventQueue.invokeLater {
        this.repaint()
      }
    }
  }

  fun setIterationLimit(limit: Long) {
    this.iterationLimit = limit
    log.debug("Set iteration limit to $limit")
    asyncUpdateMandelbrotRendering()
  }

  // TODO extract nodes computing logic somewhere else
  fun computeTreeOnce() {
    Thread {
      tree.getNodesBreadthFirst {
        it.needsCompute()
      }.parallelStream().forEach {
        it.compute()
      }
      EventQueue.invokeLater {
        this@MandelbrotPanel.repaint()
      }
    }.start()
  }

  fun getInEdgeOutSurfaces(): InEdgeOutUndef {
    val surfaces = tree.getNodesBreadthFirst {
      !it.hasChildren()
    }.groupBy {
      it.payload.status
    }.mapValues {
      it.value.map { it.position.surface }.sum()
    }

    return InEdgeOutUndef(
        surfaces[INSIDE] ?: 0.0,
        surfaces[EDGE] ?: 0.0,
        surfaces[OUTSIDE] ?: 0.0,
        surfaces[UNDEFINED] ?: 0.0
    )
  }

  companion object {
    private val log = LoggerFactory.getLogger(MandelbrotPanel::class.java)
    private val TOKEN = Object()
  }
}
