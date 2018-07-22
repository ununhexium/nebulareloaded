package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeEngine

data class MetaData(
    val iterationLimit: Long,
    val edgeSplits: Int,
    val computeEngine: MandelbrotComputeEngine
)