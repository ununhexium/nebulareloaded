package net.lab0.nebula.reloaded.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class NebulabrotPanel : JPanel() {

  val iterations = Array(0) { LongArray(0) }

  override fun paintComponent(graphics: Graphics) {
    val g2d = graphics as Graphics2D
    g2d.paint = Color.BLACK
    g2d.fillRect(0, 0, width, height)
  }
}