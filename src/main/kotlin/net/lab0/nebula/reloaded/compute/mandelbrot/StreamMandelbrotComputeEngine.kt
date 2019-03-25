package net.lab0.nebula.reloaded.compute.mandelbrot

class StreamMandelbrotComputeEngine(
    val computeEngine: MandelbrotComputeEngine,
    private val parallel: Boolean = true
) : MandelbrotComputeEngine by computeEngine {
  override fun iterationsAt(
      reals: DoubleArray,
      imgs: DoubleArray,
      iterationLimit: Long
  ): LongArray {
    val indexes = (0 until reals.size).toList().stream()
    val stream = if (parallel) indexes.parallel() else indexes
    return stream.mapToLong {
      computeEngine.iterationsAt(reals[it], imgs[it], iterationLimit)
    }.toArray()
  }
}
