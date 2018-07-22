package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.tree.PointWithIterationLimit
import net.lab0.nebula.reloaded.ui.RenderingContext

interface NebulabrotComputeEngine {
  fun compute(
      points: Iterable<PointWithIterationLimit>,
      context: RenderingContext
  )

  /**
   * Asks to stop the computation as soon as possible
   */
  fun stop()
}
