package net.lab0.nebula.reloaded.compute.mandelbrot

import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl
import net.lab0.nebula.reloaded.tree.TreeNode

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
      iterationLimit: Long = 512,
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
}