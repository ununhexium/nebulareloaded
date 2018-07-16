package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.ImageCoordinates
import net.lab0.nebula.reloaded.image.RasterizationContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

/**
 * Event management logic for the mandelbrot set viewer class.
 */
class MandelbrotActions(private val panel: MandelbrotPanel) :
    MouseListener,
    MouseMotionListener,
    KeyListener,
    MouseWheelListener,
    ComponentListener {

  companion object {
    private val log: Logger by lazy {
      LoggerFactory
          .getLogger(this::class.java.name)
    }
  }

  private var updatePositionLabelsOnMove = true
  private var press: MouseEvent? = null
  private var lastMousePosition: MouseEvent? = null

  override fun keyTyped(e: KeyEvent) {
    if (e.keyCode == KeyEvent.VK_ESCAPE) {
      updatePositionLabelsOnMove = true
    }
  }

  override fun keyPressed(e: KeyEvent) {

  }

  override fun keyReleased(e: KeyEvent) {

  }

  override fun mouseClicked(e: MouseEvent) {
    updatePositionLabelsOnMove = false
    updateLabels(e)
  }

  override fun mousePressed(e: MouseEvent) {
    press = e
  }

  override fun mouseReleased(e: MouseEvent) {
    panel.setSelectionBox(null)
  }

  override fun mouseEntered(e: MouseEvent) {

  }

  override fun mouseExited(e: MouseEvent) {

  }

  override fun mouseDragged(e: MouseEvent) {
    if (e.isControlDown) {
      val tmpPress = press
      if (tmpPress != null) {
        panel.setSelectionBox(tmpPress to e)
        panel.repaint()
      }
    }
    else {
      panel.setSelectionBox(null)
      val tmpLastMousePosition = lastMousePosition
      if (tmpLastMousePosition != null) {
        panel.moveImage(tmpLastMousePosition to e)
      }
      lastMousePosition = e
    }
  }

  override fun mouseMoved(e: MouseEvent) {
    lastMousePosition = e
    if (updatePositionLabelsOnMove) {
      updateLabels(e)
    }
  }

  private fun updateLabels(e: MouseEvent) {
    val context = RasterizationContext(
        panel.viewport,
        panel.width,
        panel.height
    )
    val imageCoordinates = ImageCoordinates(e.x, e.y)
    val planCoordinates = context.convert(imageCoordinates)

    panel.realValueLabel.text = planCoordinates.real.toString()
    panel.imgValueLabel.text = planCoordinates.img.toString()

    panel.xValueLabel.text = imageCoordinates.x.toString()
    panel.yValueLabel.text = imageCoordinates.y.toString()
  }

  override fun mouseWheelMoved(e: MouseWheelEvent) {
    panel.zoom(Math.exp(-e.preciseWheelRotation / 2))
  }

  override fun componentMoved(e: ComponentEvent?) {

  }

  override fun componentResized(e: ComponentEvent?) {
    log.debug("Component resized")
    panel.asyncUpdateMandelbrotRendering()
  }

  override fun componentHidden(e: ComponentEvent?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun componentShown(e: ComponentEvent?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
