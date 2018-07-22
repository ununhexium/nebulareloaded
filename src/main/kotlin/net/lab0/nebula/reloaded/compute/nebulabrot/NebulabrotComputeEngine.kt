package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.tree.ComplexPoint
import net.lab0.nebula.reloaded.ui.RenderingContext

interface NebulabrotComputeEngine {

  fun compute(
      points: Sequence<ComplexPoint>,
      context: RenderingContext,
      lowIterationLimit: Long
  )

  /**
   * Asks to stop the computation as soon as possible
   */
  fun stop()
}
