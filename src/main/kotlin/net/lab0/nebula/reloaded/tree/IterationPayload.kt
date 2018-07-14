package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED

class IterationPayload(var iterationLimit: Long) {

  var minIterations: Long = Long.MAX_VALUE
  var maxIterations: Long = Long.MIN_VALUE

  val processed: Boolean
    get() =
      this.minIterations != Long.MAX_VALUE &&
          this.maxIterations != Long.MIN_VALUE

  val status: PayloadStatus
    get() {
      if (!processed) return UNDEFINED
      if (minIterations >= iterationLimit) return INSIDE
      if (maxIterations < iterationLimit) return OUTSIDE
      return EDGE
    }
}