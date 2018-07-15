package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl

class PlanViewport(xRange: Pair<Number, Number>, yRange: Pair<Number, Number>) :
    Rectangle by RectangleImpl(xRange, yRange) {
    constructor(rectangle: Rectangle) : this(rectangle.xRange, rectangle.yRange)
}
