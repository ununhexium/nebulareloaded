package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext
import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeEngine
import net.lab0.nebula.reloaded.image.MandelbrotRenderer
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
import java.util.concurrent.atomic.AtomicReference

class MandelbrotPanel(computeContextRef: AtomicReference<MandelbrotComputeContext>) :
    FractalPanel(computeContextRef) {

  private var iterationLimit = 512L

  private var drawTree = true

  override fun paintComponent(graphics: Graphics) {
    super.paintComponent(graphics)
    val g = graphics as Graphics2D

    synchronized(lastRenderingRef) {
      paintLatestFractalRendering(g)
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
