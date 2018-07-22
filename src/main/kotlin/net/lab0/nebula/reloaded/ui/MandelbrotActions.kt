package net.lab0.nebula.reloaded.ui

import java.awt.event.ComponentListener
import java.awt.event.KeyListener
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelListener

/**
 * Event management logic for the mandelbrot set viewer class.
 */
class MandelbrotActions(private val mandelbrotPanel: MandelbrotPanel) :
    FractalActions(mandelbrotPanel),
    MouseListener,
    MouseMotionListener,
    KeyListener,
    MouseWheelListener,
    ComponentListener {

}
