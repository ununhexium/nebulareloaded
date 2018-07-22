package net.lab0.nebula.reloaded.compute.mandelbrot

import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl
import net.lab0.nebula.reloaded.tree.TreeNode
import net.lab0.nebula.reloaded.ui.InEdgeOutUndef

class MandelbrotComputeContext private constructor(
    val computeEngine: MandelbrotComputeEngine,
    val tree: TreeNode
) {

  @JvmOverloads constructor(
      computeEngine: MandelbrotComputeEngine = Engines.MaxParallelStreamOptim2,
      explorationArea: Rectangle = RectangleImpl(
          -2 to 2,
          -2 to 2
      ),
      iterationLimit: Long = 4096,
      edgeSplit: Int = 8
  ) : this(
      computeEngine,
      TreeNode(
          explorationArea,
          MetaData(iterationLimit, edgeSplit, computeEngine)
      )
  )

  fun changeComputeEngine(computeEngine: MandelbrotComputeEngine) =
      MandelbrotComputeContext(computeEngine, this.tree)

  fun computeTreeOnce(finishedCallback: () -> Unit) {
    Thread {
      tree.getNodesBreadthFirst {
        it.needsCompute()
      }.parallelStream().forEach {
        it.compute()
      }
      finishedCallback.invoke()
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
}