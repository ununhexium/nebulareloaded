package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext
import net.lab0.nebula.reloaded.compute.nebulabrot.NebulabrotComputeEngine
import net.lab0.nebula.reloaded.compute.nebulabrot.ParallelStreamNebulabrotComputeEngine
import net.lab0.nebula.reloaded.tree.ComplexPoint
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.TreeNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.buildSequence

class NebulabrotPanel(computeContextRef: AtomicReference<MandelbrotComputeContext>) :
    FractalPanel(computeContextRef) {

  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  val computeEngine: NebulabrotComputeEngine = ParallelStreamNebulabrotComputeEngine()

  /**
   * Complex plan points per pixel
   */
  val resolution = 0.001
  val pointsPerSide = 16
  var minIterations = 100
  var maxIterations = 10000

  override fun doRendering() {

    log.debug("Rendering Nebulabrot")

    val computeContext = computeContextRef.get()

    fun TreeNode.tooSmall(): Boolean {
      return this.position.width < resolution * pointsPerSide
    }

    val nodes = computeContextRef.get().tree.getNodesBreadthFirst(
        depthFilter = {
          it.payload.status != INSIDE &&
              it.payload.maxIterations > this.minIterations
        },
        filter = {
          it.payload.status != INSIDE &&
              (!it.hasChildren() || it.tooSmall())
        }
    )

    val points = buildSequence {
      nodes.forEach { node ->
        val position = node.position
        val minX = position.minX
        val minY = position.minY

        (0 until (position.width / resolution).toLong()).forEach { j ->
          (0 until (position.height / resolution).toLong()).forEach { i ->
            /*
             * FIXME: ideally we should use the points which are in the node
             * and aligned with the whole grid of points instead of assuming
             * that the origin of the node is already aligned,
             * but that's more computation for not much in the end
             */
            val real = minX + resolution * i
            val img = minY + resolution * j
            yield(ComplexPoint(real, img))
          }
        }
      }
    }

    val renderingContext = RenderingContext(
        viewport, width, height, 512, computeContext.computeEngine
    )
    computeEngine.compute(points, renderingContext, minIterations)
    lastRenderingRef.set(renderingContext)
  }

  override fun paintComponent(graphics: Graphics) {
    val g2d = graphics as Graphics2D
    g2d.paint = Color.BLACK
    g2d.fillRect(0, 0, width, height)
    val lastRendering = lastRenderingRef.get()
    if (lastRendering != null) {
      paintLatestFractalRendering(g2d)
    }
  }


}