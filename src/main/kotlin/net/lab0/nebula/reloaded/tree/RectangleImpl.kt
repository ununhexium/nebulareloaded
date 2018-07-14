package net.lab0.nebula.reloaded.tree

data class RectangleImpl(
    override val minX: Double,
    override val maxX: Double,
    override val minY: Double,
    override val maxY: Double
) : Rectangle {
  constructor(
      minX: Number,
      maxX: Number,
      minY: Number,
      maxY: Number
  ) : this(minX.toDouble(), maxX.toDouble(), minY.toDouble(), maxY.toDouble())
}