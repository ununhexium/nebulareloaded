package net.lab0.nebula.reloaded.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class SurfaceIndicator(
    var inside: Double,
    var edge: Double,
    var outside: Double,
    var undefined: Double
) : JPanel() {

    constructor() : this(1.0, 1.0, 1.0, 1.0)

    override fun paintComponent(graphics: Graphics) {
        val g = graphics as Graphics2D
        val sum = Math.max(inside + edge + outside + undefined, 1.0)
        val endInside = width * inside / sum
        val endEdge = width * (inside + edge) / sum
        val endOutside = width * (inside + edge + outside) / sum

        g.paint = Color(200, 0, 0)
        g.fillRect(0, 0, this.width, this.height)

        g.paint = Color(0, 200, 0)
        g.fillRect(endInside.toInt(), 0, this.width, this.height)

        g.paint = Color(0, 0, 255)
        g.fillRect(endEdge.toInt(), 0, this.width, this.height)

        g.paint = Color(100, 100, 100)
        g.fillRect(endOutside.toInt(), 0, this.width, this.height)
    }
}
