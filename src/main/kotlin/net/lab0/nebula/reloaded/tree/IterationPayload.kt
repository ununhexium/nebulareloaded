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
      /*
       * TODO: Compute smoothness evenness / gradient instead of only the
       */

      /**
       * Only consider a node as outside if it's smooth.
       * That is, if the variations in it are small.
       * This is more reliable than checking the max iteration value as
       * a high difference between min and max indicates that an edge is near.
       */
      val smoothness = 4
      if (maxIterations - minIterations < smoothness) return OUTSIDE
      return EDGE
    }
}