package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.mandelbrot.Compute

data class MetaData(
    val iterationLimit: Long,
    val edgeSplits: Int,
    val computeEngine: Compute
)