package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.compute.mandelbrot.Engines.Default
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
import javax.swing.JLabel

open class FractalActions(private val panel: FractalPanel) :
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

    private val computeEngine = Default
  }

  private var updatePositionLabelsOnMove = true

  private var lastMousePosition: MouseEvent? = null
  private var press: MouseEvent? = null

  // references to the main panel
  lateinit var realValueLabel: JLabel
  lateinit var imgValueLabel: JLabel
  lateinit var iterationsValueLabel: JLabel
  lateinit var xValueLabel: JLabel
  lateinit var yValueLabel: JLabel

  override fun mouseReleased(e: MouseEvent) {
    panel.asyncUpdateRendering()
  }

  override fun mouseEntered(e: MouseEvent) {

  }

  override fun mouseClicked(e: MouseEvent) {
    if (e.clickCount > 1) {
      updatePositionLabelsOnMove = !updatePositionLabelsOnMove
    }

    updateLabels(e)
  }

  override fun mouseExited(e: MouseEvent) {
  }

  override fun mousePressed(e: MouseEvent) {
    press = e
  }

  override fun mouseMoved(e: MouseEvent) {
    lastMousePosition = e
    if (updatePositionLabelsOnMove) {
      updateLabels(e)
    }
  }

  override fun mouseDragged(e: MouseEvent) {
    val tmpLastMousePosition = lastMousePosition
    if (tmpLastMousePosition != null) {
      panel.moveViewport(tmpLastMousePosition to e)
    }
    lastMousePosition = e
  }

  override fun keyTyped(e: KeyEvent) {

  }

  override fun keyPressed(e: KeyEvent) {
  }

  override fun keyReleased(e: KeyEvent) {
  }

  override fun mouseWheelMoved(e: MouseWheelEvent) {
    panel.zoom(Math.exp(-e.preciseWheelRotation / 2))
  }

  override fun componentMoved(e: ComponentEvent) {
  }

  override fun componentResized(e: ComponentEvent) {
    log.debug("Component resized")
    panel.asyncUpdateRendering()
  }

  override fun componentHidden(e: ComponentEvent) {
  }

  override fun componentShown(e: ComponentEvent) {
  }

  private fun updateLabels(e: MouseEvent) {
    val context = RasterizationContext(
        panel.viewport,
        panel.width,
        panel.height
    )
    val planCoordinates = context.convertImageToPlan(e.x, e.y)

    realValueLabel.text = planCoordinates.real.toString()
    imgValueLabel.text = planCoordinates.img.toString()
    iterationsValueLabel.text = computeEngine
        .iterationsAt(
            planCoordinates.real,
            planCoordinates.img,
            panel.computeContextRef.get().tree.metadata.iterationLimit
        ).toString()

    xValueLabel.text = e.x.toString()
    yValueLabel.text = e.y.toString()
  }
}
