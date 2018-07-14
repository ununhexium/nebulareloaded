package net.lab0.nebula.reloaded.mandelbrot;

/**
 * Reference, simple computation method.
 */
public class DefaultCompute implements Compute {
  @Override
  public long iterationsAt(double real, double img, long maxIter) {
    double real1 = real;
    double img1 = img;
    double real2;
    double img2;

    long iter = 0;
    while ((iter < maxIter) && ((real1 * real1 + img1 * img1) < 4.0d)) {
      real2 = real1 * real1 - img1 * img1 + real;
      img2 = 2 * real1 * img1 + img;

      real1 = real2;
      img1 = img2;

      iter++;
    }

    return iter;
  }
}
