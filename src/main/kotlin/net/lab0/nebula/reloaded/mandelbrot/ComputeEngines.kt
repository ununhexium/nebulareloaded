package net.lab0.nebula.reloaded.mandelbrot

object Engines{
  object Default :
      ComputeEngine by DefaultComputeEngine()

  object Optim2 :
      ComputeEngine by ComputeEngineOptim2()

  object MaxParallelStreamOptim2 :
      ComputeEngine by StreamComputeEngine(Optim2)

  object MaxParallelThreadOptim2 :
      ComputeEngine by ThreadComputeEngine(Optim2)

  // TODO get list by reflection
  val computeEngines = listOf(
      Default, Optim2, MaxParallelStreamOptim2, MaxParallelThreadOptim2
  )
}