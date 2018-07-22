package net.lab0.nebula.reloaded.compute.mandelbrot

import java.util.stream.IntStream

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
    val indexes = IntStream.range(0, real.size)
    val stream = if (parallel) indexes.parallel() else indexes
    return stream.mapToLong {
      computeEngine.iterationsAt(real[it], img[it], iterationLimit)
    }.toArray()
  }

}
