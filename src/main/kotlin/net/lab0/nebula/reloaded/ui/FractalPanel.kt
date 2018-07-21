package net.lab0.nebula.reloaded.ui

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import javax.swing.JPanel

abstract class FractalPanel : JPanel() {
  companion object {
    private val TOKEN = Object()
  }

  /**
   * Event store to tell that an image update event has been received.
   */
  private val blockingQueue = ArrayBlockingQueue<Any>(1)
  /**
   * Holder for a single thread: the one is charge of checking that an image update request has been received.
   */
  private val imageUpdateWatcher = Executors.newSingleThreadExecutor()


  init {
    asyncUpdateRendering()
    imageUpdateWatcher.execute(ImageUpdateWatcher(this, blockingQueue))
    /*
     * We only want to notify.
     * If a value was already present, then notifying again will not change anything.
     */
    triggerRefresh()
  }

  /**
   * Updates the graphic rendering of the component in an asynchronous way.
   */
  open fun asyncUpdateRendering() {
    triggerRefresh()
  }

  fun triggerRefresh() {
    blockingQueue.offer(TOKEN)
  }


  internal abstract fun doRendering()
}