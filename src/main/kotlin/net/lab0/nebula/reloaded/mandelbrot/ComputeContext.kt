package net.lab0.nebula.reloaded.mandelbrot

import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl
import net.lab0.nebula.reloaded.tree.TreeNode

class ComputeContext private constructor(
    val computeEngine: ComputeEngine,
    val tree: TreeNode
) {

  constructor(
      computeEngine: ComputeEngine = Engines.MaxParallelStreamOptim2,
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

  fun changeComputeEngine(computeEngine: ComputeEngine) =
      ComputeContext(computeEngine, this.tree)
}