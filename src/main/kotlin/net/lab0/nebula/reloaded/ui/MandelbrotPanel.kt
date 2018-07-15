package net.lab0.nebula.reloaded.ui

import net.lab0.nebula.reloaded.image.ImageCoordinates
import net.lab0.nebula.reloaded.image.MandelbrotRenderer
import net.lab0.nebula.reloaded.image.PlanViewport
import net.lab0.nebula.reloaded.image.RasterizationContext
import net.lab0.nebula.reloaded.mandelbrot.ComputeOptim2
import org.slf4j.LoggerFactory
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JLabel
import javax.swing.JPanel

class MandelbrotPanel : JPanel() {

    var viewport = createDefaultViewport()
        private set

    private val compute = ComputeOptim2()

    private val iterationLimit = 64L

    lateinit var realValueLabel: JLabel
    lateinit var imgValueLabel: JLabel
    lateinit var xValueLabel: JLabel
    lateinit var yValueLabel: JLabel

    private var selectionBox: Pair<MouseEvent, MouseEvent>? = null


    private val lastRenderingRef = AtomicReference<BufferedImage>()
    /**
     * Event store to tell that an image update event has been received.
     */
    private val blockingQueue = ArrayBlockingQueue<Any>(1)
    /**
     * Holder for a single thread: the one is charge of checking that an image update request has been received.
     */
    private val imageUpdateWatcher = Executors.newSingleThreadExecutor()

    init {
        asyncUpdateMandelbrotRendering()
        imageUpdateWatcher.execute(ImageUpdateWatcher(this, blockingQueue))
        /*
         * We only want to notify.
         * If a value was already present, then notifying again will not change anything.
         */
        blockingQueue.offer(TOKEN) // NOSONAR
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g = graphics as Graphics2D
        synchronized(lastRenderingRef) {
            if (lastRenderingRef.get() != null) {
                g.drawRenderedImage(lastRenderingRef.get(), AffineTransform())
            }
        }
        if (selectionBox != null) {

        }
    }

    /**
     * Adds a flag to tell that the image has to be updated.
     */
    fun asyncUpdateMandelbrotRendering() {
        blockingQueue.offer(TOKEN)
    }

    internal fun updateMandelbrotRendering() {
        if (this.width == 0 || this.height == 0) {
            // skip because can't create an image
            return
        }

        val renderer = MandelbrotRenderer(viewport)
        val image = renderer.render(
            this.width,
            this.height,
            iterationLimit,
            compute
        )
        synchronized(lastRenderingRef) {
            lastRenderingRef.set(image)
        }
    }

    internal fun setxValueLabel(xValueLabel: JLabel) {
        this.xValueLabel = xValueLabel
    }

    internal fun setyValueLabel(yValueLabel: JLabel) {
        this.yValueLabel = yValueLabel
    }

    fun setSelectionBox(startToEnd: Pair<MouseEvent, MouseEvent>?) {
        this.selectionBox = startToEnd
    }

    fun moveImage(movement: Pair<MouseEvent, MouseEvent>) {
        val context = RasterizationContext(viewport, width, height)
        val from = ImageCoordinates(movement.first.x, movement.first.y)
        val to = ImageCoordinates(movement.second.x, movement.second.y)

        val oldPosition = context.convert(from)
        val newPosition = context.convert(to)
        viewport = viewport.translate(oldPosition.minus(newPosition))
        asyncUpdateMandelbrotRendering()
    }

    fun resetViewport() {
        viewport = createDefaultViewport()
        asyncUpdateMandelbrotRendering()
    }

    private fun createDefaultViewport(): PlanViewport {
        return PlanViewport(
            Pair(-2, 2), Pair(-2, 2)
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(MandelbrotPanel::class.java)
        private val TOKEN = Object()
    }
}
