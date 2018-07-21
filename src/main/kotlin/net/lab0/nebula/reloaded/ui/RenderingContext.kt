package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.mandelbrot.ComputeEngine
import net.lab0.nebula.reloaded.tree.Rectangle
import java.awt.image.BufferedImage

class RenderingContext(
    val viewport: Rectangle,
    val width: Int,
    val height:Int,
    val iterationLimit: Long,
    val computeEngine: ComputeEngine
){
  lateinit var rendering: BufferedImage
}