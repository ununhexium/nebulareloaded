package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.MandelbrotComputeContext
import net.lab0.nebula.reloaded.image.ImageCoordinates
import net.lab0.nebula.reloaded.image.PlanViewport
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.tree.Rectangle
import net.lab0.nebula.reloaded.tree.RectangleImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JPanel
import javax.swing.event.EventListenerList

abstract class FractalPanel(
    val computeContextRef: AtomicReference<MandelbrotComputeContext>
) : JPanel() {
  companion object {
    private val TOKEN = Object()

    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  val eventListenersList = EventListenerList()

  var viewport = createDefaultViewport()
    set(new) {
      if (new != viewport) {
        field = new
        fireViewportUpdated()
      }
    }

  private fun fireViewportUpdated() {
    eventListenersList.getListeners(ViewportListener::class.java).forEach {
      it.viewportChanged(this, viewport)
    }
  }

  private var drawFractal = true

  protected val lastRenderingRef = AtomicReference<RenderingContext>()
  /**
   * Event store to tell that an image update event has been received.
   */
  private val blockingQueue = ArrayBlockingQueue<Any>(1)
  /**
   * Holder for a single thread: the one is charge of checking that an image update request has been received.
   */
  private val imageUpdateWatcher = Executors.newSingleThreadExecutor()


  init {
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


  protected fun paintLatestFractalRendering(
      g: Graphics2D
  ) {
    if (drawFractal && lastRenderingRef.get() != null) {
      /**
       * compute the position difference between the last rendering and
       * the current rendering. This is necessary as the repaint may happen
       * between the moment the image moved and the image is recomputed.
       *
       * If we are in such a situation, repaint the part of the image that
       * can be updated while waiting for the next image to be rendered.
       */

      val rasterizationContext = getRasterizationContext()

      val lastViewport = lastRenderingRef.get().viewport
      val previousCenter = rasterizationContext.convert(lastViewport.center)
      val currentCenter = rasterizationContext.convert(viewport.center)

      val xOffset = previousCenter.x - currentCenter.x
      val yOffset = previousCenter.y - currentCenter.y

      log.debug("Drawing offset: $xOffset;$yOffset")

      val affineTransform = AffineTransform(
          1.0, // no X scaling
          0.0, // no Y shearing
          0.0, // no X shearing
          1.0, // no Y scaling
          xOffset.toDouble(),
          yOffset.toDouble()
      )
      g.drawRenderedImage(lastRenderingRef.get().rendering, affineTransform)
    }
  }

  fun setShowFractal(enabled: Boolean) {
    this.drawFractal = enabled
    if (this.drawFractal) {
      asyncUpdateRendering()
    }
    else {
      EventQueue.invokeLater {
        this.repaint()
      }
    }
  }

  fun addViewportListener(listener: ViewportListener) {
    eventListenersList.add(ViewportListener::class.java, listener)
  }
}