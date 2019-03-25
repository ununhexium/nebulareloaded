package net.lab0.nebula.reloaded.compute.mandelbrot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CudaMandelbrotComputeEngineTest {
  @Test
  fun `can run a GPU computation`() {
    val c = CudaMandelbrotComputeEngine()
    c.iterationsAt(0.0, 0.0, 128)
  }

  @Test
  fun `can run multiple GPU computations`() {
    val c = CudaMandelbrotComputeEngine()
    repeat(10){
      c.iterationsAt(0.0, 0.0, 128)
    }
  }
}
