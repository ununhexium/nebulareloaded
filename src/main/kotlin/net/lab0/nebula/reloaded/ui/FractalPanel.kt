package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.ImageCoordinates
import net.lab0.nebula.reloaded.image.PlanViewport
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl
import java.awt.event.MouseEvent
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import javax.swing.JPanel

abstract class FractalPanel : JPanel() {
  companion object {
    private val TOKEN = Object()
  }

  var viewport = createDefaultViewport()
    private set

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

  private fun createDefaultViewport(): PlanViewport {
    return PlanViewport(
        Pair(-2, 2), Pair(-2, 2)
    )
  }

  fun resetViewport() {
    viewport = createDefaultViewport()
    asyncUpdateRendering()
  }

  fun zoom(factor: Double) {
    viewport = viewport.zoom(factor)
    asyncUpdateRendering()
  }

  fun moveViewport(movement: Pair<MouseEvent, MouseEvent>) {
    val context = RasterizationContext(viewport, width, height)
    val from = ImageCoordinates(movement.first.x, movement.first.y)
    val to = ImageCoordinates(movement.second.x, movement.second.y)

    val oldPosition = context.convert(from)
    val newPosition = context.convert(to)
    viewport = viewport.translate(oldPosition.minus(newPosition))
    repaint()
    asyncUpdateRendering()
  }

  internal fun getRasterizationContext(): RasterizationContext {
    return RasterizationContext(
        viewport,
        this.width,
        this.height
    )
  }

  fun getActualViewport(): Rectangle {
    val rasterizationContext = getRasterizationContext()
    val topLeft = rasterizationContext.convertImageToPlan(0, 0)
    val bottomRight = rasterizationContext.convertImageToPlan(width, height)
    return RectangleImpl(
        topLeft.real to bottomRight.real, bottomRight.img to topLeft.img
    )
  }

  internal abstract fun doRendering()
}