package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.ComputeContext
import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeEngine
import net.lab0.nebula.reloaded.image.MandelbrotRenderer
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.image.withAlpha
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
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JLabel

class MandelbrotPanel(computeContextRef: AtomicReference<MandelbrotComputeContext>) :
    FractalPanel(computeContextRef) {

  private var iterationLimit = 512L

  lateinit var realValueLabel: JLabel
  lateinit var imgValueLabel: JLabel
  lateinit var iterationsValueLabel: JLabel
  lateinit var xValueLabel: JLabel
  lateinit var yValueLabel: JLabel

  private var drawFractal = true
  private var drawTree = true

  private var selectionBox: Pair<MouseEvent, MouseEvent>? = null

  private val lastRenderingRef = AtomicReference<RenderingContext>()


  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val g = graphics as Graphics2D
    val rasterizationContext = getRasterizationContext()

    synchronized(lastRenderingRef) {
      paintLatestFractalRendering(rasterizationContext, g)
    }

    if (drawTree) {
      paintLatestTree(g)
    }
  }

  private fun paintLatestTree(g: Graphics2D) {
    val tooSmallRatio = 32
    val actualViewport = getActualViewport()

    fun TreeNode.isTooSmall() =
        this.position.width < actualViewport.width / tooSmallRatio &&
            this.position.height < actualViewport.height / tooSmallRatio

    val toRender = computeContextRef.get().tree.getNodesBreadthFirst(
        depthFilter = {
          !it.isTooSmall() &&
              it.position.overlaps(actualViewport)
        },
        filter = {
          it.position.overlaps(actualViewport) &&
              (!it.hasChildren() || it.isTooSmall())
        }
    )
    log.debug("Found ${toRender.size} nodes to render")
    renderAreas(g, toRender)
  }

  private fun paintLatestFractalRendering(
      rasterizationContext: RasterizationContext,
      g: Graphics2D
  ) {
    if (drawFractal && lastRenderingRef.get() != null) {
      /**
       * compute the position difference between the last rendering and
       * the current rendering. This is necessary as the repaint may happen
       * between the moment the image moved and the image is recomputed.
       *
       * If we are in such a situation, repaint the part of the image that
       * can be updated while waiting for the next image to be rendered.
       */

      val lastViewport = lastRenderingRef.get().viewport
      val previousCenter = rasterizationContext.convert(lastViewport.center)
      val currentCenter = rasterizationContext.convert(viewport.center)

      val xOffset = previousCenter.x - currentCenter.x
      val yOffset = previousCenter.y - currentCenter.y

      log.debug("Drawing offset: $xOffset;$yOffset")

      val affineTransform = AffineTransform(
          1.0, // no X scaling
          0.0, // no Y shearing
          0.0, // no X shearing
          1.0, // no Y scaling
          xOffset.toDouble(),
          yOffset.toDouble()
      )
      g.drawRenderedImage(lastRenderingRef.get().rendering, affineTransform)
    }
  }

  private fun renderAreas(
      g: Graphics2D,
      nodes: List<TreeNode>
  ) {
    val rasterizationContext = getRasterizationContext()

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

  override fun doRendering() {
    log.debug("Update rendering")
    if (this.width == 0 || this.height == 0) {
      // skip because can't create an image
      return
    }

    val renderingContext = RenderingContext(
        viewport,
        width,
        height,
        iterationLimit,
        computeContextRef.get().computeEngine
    )
    val renderer = MandelbrotRenderer(renderingContext)
    renderer.render()
    synchronized(lastRenderingRef) {
      lastRenderingRef.set(renderingContext)
    }
  }

  fun setSelectionBox(startToEnd: Pair<MouseEvent, MouseEvent>?) {
    this.selectionBox = startToEnd
  }

  fun setComputeEngine(computeEngine: MandelbrotComputeEngine) {
    log.debug("Switching compute engine to $computeEngine")
    object : Thread() {
      override fun run() {
        this@MandelbrotPanel.computeContextRef.set(
            this@MandelbrotPanel.computeContextRef.get()
                .changeComputeEngine(computeEngine)
        )
      }
    }.start()
  }

  fun setShowFractal(enabled: Boolean) {
    this.drawFractal = enabled
    if (this.drawFractal) {
      asyncUpdateRendering()
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
      asyncUpdateRendering()
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
    asyncUpdateRendering()
  }

  fun getIterationLimit() = this.iterationLimit

  // TODO extract nodes computing logic somewhere else
  fun computeTreeOnce() {
    Thread {
      computeContextRef.get().tree.getNodesBreadthFirst {
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
    val surfaces = computeContextRef.get().tree.getNodesBreadthFirst {
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
  }
}
