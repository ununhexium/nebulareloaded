package net.lab0.nebula.reloaded.mandelbrot

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class ThreadComputeEngine(
    private val computeEngine: ComputeEngine,
    private val maxThreadCount: Int = Runtime.getRuntime().availableProcessors()
) : ComputeEngine {
    private val executor = Executors.newFixedThreadPool(maxThreadCount)

    init {
        if (maxThreadCount < 1) {
            throw IllegalArgumentException("The number of threads must be >= 1.")
        }
    }

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
        val result = LongArray(real.size)
        val tasks = (0..maxThreadCount).map { offset ->
            Callable {
                (offset until real.size step maxThreadCount).forEach {
                    result[it] = computeEngine
                        .iterationsAt(real[it], img[it], iterationLimit)
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
