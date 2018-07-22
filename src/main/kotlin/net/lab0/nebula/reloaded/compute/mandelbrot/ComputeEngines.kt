package net.lab0.nebula.reloaded.compute.mandelbrot

object Engines {
  object Default :
      MandelbrotComputeEngine by DefaultMandelbrotComputeEngine() {
    override fun toString() = "Default"
  }

  object Optim2 :
      MandelbrotComputeEngine by MandelbrotComputeEngineOptim2() {
    override fun toString() = "Optim2"
  }

  object MaxParallelStreamOptim2 :
      MandelbrotComputeEngine by StreamMandelbrotComputeEngine(Optim2) {
    override fun toString() = "Stream Optim2"
  }

  object MaxParallelThreadOptim2 :
      MandelbrotComputeEngine by ThreadMandelbrotComputeEngine(Optim2) {
    override fun toString() = "Thread Optim2"
  }

  // TODO get list by reflection
  val computeEngines = listOf(
      Default, Optim2, MaxParallelStreamOptim2, MaxParallelThreadOptim2
  )
}