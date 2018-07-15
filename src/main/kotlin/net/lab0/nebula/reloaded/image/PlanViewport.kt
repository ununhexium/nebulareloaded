package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl

class PlanViewport(xRange: Pair<Number, Number>, yRange: Pair<Number, Number>) :
    Rectangle by RectangleImpl(xRange, yRange) {
    constructor(rectangle: Rectangle) : this(rectangle.xRange, rectangle.yRange)

    fun zoom(factor: Double): PlanViewport {

        //how from the center will the area extend
        val xExtent = this.width / factor / 2
        val yExtent = this.height / factor / 2

        return PlanViewport(
            (this.midX - xExtent) to (this.midX + xExtent),
            (this.midY - yExtent) to (this.midY + yExtent)
        )
    }

    /**
     * Translates the plan by the [vector] distance.
     */
    fun translate(vector: PlanCoordinates) =
        PlanViewport(
            this.minX + vector.real to this.maxX + vector.real,
            this.minY + vector.img to this.maxY + vector.img
        )
}
