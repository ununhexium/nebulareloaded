package net.lab0.nebula.reloaded.compute.mandelbrot

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class ThreadMandelbrotComputeEngine(
    private val computeEngine: MandelbrotComputeEngine,
    private val maxThreadCount: Int = Runtime.getRuntime().availableProcessors()
) : MandelbrotComputeEngine by computeEngine {
  private val executor = Executors.newFixedThreadPool(maxThreadCount)

  init {
    if (maxThreadCount < 1) {
      throw IllegalArgumentException("The number of threads must be >= 1.")
    }
  }

  override fun iterationsAt(
      reals: DoubleArray,
      imgs: DoubleArray,
      iterationLimit: Long
  ): LongArray {
    val result = LongArray(reals.size)
    val tasks = (0..maxThreadCount).map { offset ->
      Callable {
        (offset until reals.size step maxThreadCount).forEach {
          result[it] = computeEngine
              .iterationsAt(reals[it], imgs[it], iterationLimit)
        }
      }
    }

    val futures = executor.invokeAll(tasks)
    futures.forEach {
      it.get()
    }

    return result;
  }
}
