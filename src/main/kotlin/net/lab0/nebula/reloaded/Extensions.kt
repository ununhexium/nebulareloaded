package net.lab0.nebula.reloaded



fun <A : Number, B : Number> Pair<A, B>.toDouble() =
    Pair(this.first.toDouble(), this.second.toDouble())

fun Pair<Double, Double>.min() =
    Math.min(this.first, this.second)

fun Pair<Double, Double>.max() =
    Math.max(this.first, this.second)
