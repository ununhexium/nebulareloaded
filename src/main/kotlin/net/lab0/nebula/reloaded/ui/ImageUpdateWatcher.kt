package net.lab0.nebula.reloaded.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.util.concurrent.BlockingQueue

/**
 * Task in charge of watching for image update events and executing them.
 */
class ImageUpdateWatcher(
    private val panel: FractalPanel,
    private val queue: BlockingQueue<Any>
) : Runnable {

  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  var stop = false

  override fun run() {
    while (!stop) {
      try {
        log.debug("Waiting for an image refresh event")
        queue.take()
        panel.doRendering()
        EventQueue.invokeAndWait {
          panel.repaint()
        }
      }
      catch (e: InterruptedException) {
        log.warn(
            "Interrupted when waiting for the next image update event",
            e
        )
        stop = true
        Thread.currentThread().interrupt()
      }
    }
  }
}
