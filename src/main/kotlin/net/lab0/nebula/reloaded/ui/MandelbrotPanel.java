package net.lab0.nebula.reloaded.ui;

import kotlin.Pair;
import net.lab0.nebula.reloaded.image.ImageCoordinates;
import net.lab0.nebula.reloaded.image.MandelbrotRenderer;
import net.lab0.nebula.reloaded.image.PlanCoordinates;
import net.lab0.nebula.reloaded.image.PlanViewport;
import net.lab0.nebula.reloaded.image.RasterizationContext;
import net.lab0.nebula.reloaded.mandelbrot.Compute;
import net.lab0.nebula.reloaded.mandelbrot.ComputeOptim2;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MandelbrotPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(MandelbrotPanel.class);
    private static final Object TOKEN = new Object();

    private PlanViewport viewport = createDefaultViewport();

    private Compute compute = new ComputeOptim2();

    private long iterationLimit = 64L;

    private JLabel realValueLabel;
    private JLabel imgValueLabel;
    private JLabel xValueLabel;
    private JLabel yValueLabel;
    private Pair<MouseEvent, MouseEvent> selectionBox = null;


    private final AtomicReference<BufferedImage> lastRenderingRef = new AtomicReference<>();
    /**
     * Event store to tell that an image update event has been received.
     */
    private final BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(1);
    /**
     * Holder for a single thread: the one is charge of checking that an image update request has been received.
     */
    private final Executor imageUpdateWatcher = Executors.newSingleThreadExecutor();

    public MandelbrotPanel() {
        asyncUpdateMandelbrotRendering();
        imageUpdateWatcher.execute(new ImageUpdateWatcher(this, blockingQueue));
        /*
         * We only want to notify.
         * If a value was already present, then notifying again will not change anything.
         */
        blockingQueue.offer(TOKEN); // NOSONAR
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        synchronized (lastRenderingRef) {
            if (lastRenderingRef.get() != null) {
                g.drawRenderedImage(lastRenderingRef.get(), new AffineTransform());
            }
        }
        if (selectionBox != null) {

        }
    }

    /**
     * Adds a flag to tell that the image has to be updated.
     */
    public void asyncUpdateMandelbrotRendering() {
        blockingQueue.offer(TOKEN);
    }

    void updateMandelbrotRendering() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            // skip because can't create and image
            return;
        }

        MandelbrotRenderer renderer = new MandelbrotRenderer(viewport);
        BufferedImage image = renderer.render(
            this.getWidth(),
            this.getHeight(),
            iterationLimit,
            compute
        );
        synchronized (lastRenderingRef) {
            lastRenderingRef.set(image);
        }
    }

    void setRealValueLabel(JLabel realValueLabel) {
        this.realValueLabel = realValueLabel;
    }

    void setImgValueLabel(JLabel imgValueLabel) {
        this.imgValueLabel = imgValueLabel;
    }

    void setxValueLabel(JLabel xValueLabel) {
        this.xValueLabel = xValueLabel;
    }

    void setyValueLabel(JLabel yValueLabel) {
        this.yValueLabel = yValueLabel;
    }

    public JLabel getRealValueLabel() {
        return realValueLabel;
    }

    public JLabel getImgValueLabel() {
        return imgValueLabel;
    }

    public JLabel getXValueLabel() {
        return xValueLabel;
    }

    public JLabel getYValueLabel() {
        return yValueLabel;
    }

    public void setSelectionBox(Pair<MouseEvent, MouseEvent> startToEnd) {
        this.selectionBox = startToEnd;
    }

    public void moveImage(@NotNull Pair<MouseEvent, MouseEvent> movement) {
        RasterizationContext context = new RasterizationContext(viewport, getWidth(), getHeight());
        PlanCoordinates oldPosition = context.convert(new ImageCoordinates(movement.getFirst().getX(), movement.getFirst().getY()));
        PlanCoordinates newPosition = context.convert(new ImageCoordinates(movement.getSecond().getX(), movement.getSecond().getY()));
        viewport = viewport.translate(oldPosition.minus(newPosition));
        asyncUpdateMandelbrotRendering();
    }

    public void resetViewport() {
        viewport = createDefaultViewport();
        asyncUpdateMandelbrotRendering();
    }

    private PlanViewport createDefaultViewport() {
        return new PlanViewport(
            new Pair<>(-2, 2), new Pair<>(-2, 2)
        );
    }

    public PlanViewport getViewport() {
        return viewport;
    }
}
