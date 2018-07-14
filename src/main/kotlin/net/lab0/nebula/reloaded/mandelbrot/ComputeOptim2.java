package net.lab0.nebula.reloaded.mandelbrot;

/**
 * Same as <code>computeIterationsCountReference()</code> with an optimization to test if inside or outside every 2
 * iterations.
 */
public class ComputeOptim2 implements Compute {
  @Override
  public long iterationsAt(double real, double img, long maxIter) {
    double realsqr = real * real;
    double imgsqr = img * img;

    double real1 = real;
    double img1 = img;
    double real2;
    double img2;

    long iter = 0;
    while (
        (iter < maxIter)
        & // NOSONAR this is an intended optimisation. TODO check this is still faster
        ((realsqr + imgsqr) < 4.0d)
        ) {
      real2 = real1 * real1 - img1 * img1 + real;
      img2 = 2 * real1 * img1 + img;

      realsqr = real2 * real2;
      imgsqr = img2 * img2;
      real1 = realsqr - imgsqr + real;
      img1 = 2 * real2 * img2 + img;

      iter += 2;
    }

    return iter;
  }
}
