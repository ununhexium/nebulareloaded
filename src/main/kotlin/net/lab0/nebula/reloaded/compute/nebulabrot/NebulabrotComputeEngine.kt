package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.tree.ComplexPoint
import net.lab0.nebula.reloaded.ui.RenderingContext

interface NebulabrotComputeEngine {

  fun defaultColorBounds(
      lowIterationLimit: Long,
      highIterationLimit: Long
  ): List<Long> {
    val span = highIterationLimit - lowIterationLimit
    val spread = Math.pow(span.toDouble(), 1 / 3.0)
    val steps = listOf(0.0, spread, spread * spread, spread * spread * spread)
        .map { it.toLong() + lowIterationLimit }
    return steps.drop(1)
  }

  /**
   * Asks to stop the computation as soon as possible
   */
  fun stop()

  fun compute(
      points: Sequence<ComplexPoint>,
      context: RenderingContext,
      lowIterationLimit: Long,
      colorBounds: List<Long> =
          defaultColorBounds(
              lowIterationLimit,
              context.iterationLimit
          ),
      /**
       * Maps points from the min-max interval to the 0-255 interval.
       * Defaults to square root interpolation.
       */
      interpolation: (min: Long, average: Long, max: Long, value: Long) -> Long =
          { min, _, max, value ->
            val rangeCandidate = max - min
            val range = if (rangeCandidate == 0L) 1.0
            else Math.sqrt(rangeCandidate.toDouble())

            (255 * Math.sqrt((value - min).toDouble()) / range).toLong()
          }
  )
}
