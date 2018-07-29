package net.lab0.nebula.reloaded.compute.mandelbrot

class StreamMandelbrotComputeEngine(
    val computeEngine: MandelbrotComputeEngine,
    private val parallel: Boolean = true
) : MandelbrotComputeEngine {
  override fun iterationsAt(
      real: Double,
      img: Double,
      iterationLimit: Long
  ): Long {
    TODO("not implemented")
  }

  override fun iterationsAt(
      real: DoubleArray,
      img: DoubleArray,
      iterationLimit: Long
  ): LongArray {
    val indexes = (0 until real.size).toList().stream()
    val stream = if (parallel) indexes.parallel() else indexes
    return stream.mapToLong {
      computeEngine.iterationsAt(real[it], img[it], iterationLimit)
    }.toArray()
  }

}
