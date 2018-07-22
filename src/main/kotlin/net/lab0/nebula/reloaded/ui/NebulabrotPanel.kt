package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.concurrent.atomic.AtomicReference

class NebulabrotPanel(computeContextRef: AtomicReference<MandelbrotComputeContext>) :
    FractalPanel(computeContextRef) {

  override fun doRendering() {

  }

  val iterations = Array(0) { LongArray(0) }

  override fun paintComponent(graphics: Graphics) {
    val g2d = graphics as Graphics2D
    g2d.paint = Color.BLACK
    g2d.fillRect(0, 0, width, height)
  }


}