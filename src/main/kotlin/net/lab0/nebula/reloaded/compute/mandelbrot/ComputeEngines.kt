package net.lab0.nebula.reloaded.compute.mandelbrot

object Engines{
  object Default :
      MandelbrotComputeEngine by DefaultMandelbrotComputeEngine()

  object Optim2 :
      MandelbrotComputeEngine by MandelbrotComputeEngineOptim2()

  object MaxParallelStreamOptim2 :
      MandelbrotComputeEngine by StreamMandelbrotComputeEngine(Optim2)

  object MaxParallelThreadOptim2 :
      MandelbrotComputeEngine by ThreadMandelbrotComputeEngine(Optim2)

  // TODO get list by reflection
  val computeEngines = listOf(
      Default, Optim2, MaxParallelStreamOptim2, MaxParallelThreadOptim2
  )
}