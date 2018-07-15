package net.lab0.nebula.reloaded.ui;

import kotlin.Pair;
import net.lab0.nebula.reloaded.image.MandelbrotRenderer;
import net.lab0.nebula.reloaded.image.PlanViewport;
import net.lab0.nebula.reloaded.mandelbrot.Compute;
import net.lab0.nebula.reloaded.mandelbrot.ComputeOptim2;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MandelbrotPanel extends JPanel {

    PlanViewport viewport = new PlanViewport(
        new Pair<>(-2, 2), new Pair<>(-2, 2)
    );

    Compute compute = new ComputeOptim2();

    long iterationLimit = 64L;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        MandelbrotRenderer renderer = new MandelbrotRenderer(viewport);
        BufferedImage image = renderer.render(this.getWidth(), this.getHeight(), iterationLimit, compute);

        g.drawRenderedImage(image, AffineTransform.getRotateInstance(0.0));
    }
}
