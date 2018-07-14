package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.max
import net.lab0.nebula.reloaded.min
import net.lab0.nebula.reloaded.toDouble

data class RectangleImpl private constructor(
    override val minX: Double,
    override val maxX: Double,
    override val minY: Double,
    override val maxY: Double
) : Rectangle {

  constructor(
      xRange: Pair<Number, Number>,
      yRange: Pair<Number, Number>
  ) : this(
      xRange.toDouble().min(),
      xRange.toDouble().max(),
      yRange.toDouble().min(),
      yRange.toDouble().max()
  )
}