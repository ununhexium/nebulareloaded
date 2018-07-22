package net.lab0.nebula.reloaded.compute.mandelbrot;

/**
 * Reference, simple computation method. CPU implementation.
 */
public class DefaultMandelbrotComputeEngine implements MandelbrotComputeEngine {
  @Override
  public long iterationsAt(double real, double img, long iterationLimit) {
    double real1 = real;
    double img1 = img;
    double real2;
    double img2;

    long iter = 0;
    while ((iter < iterationLimit) && ((real1 * real1 + img1 * img1) < 4.0d)) {
      real2 = real1 * real1 - img1 * img1 + real;
      img2 = 2 * real1 * img1 + img;

      real1 = real2;
      img1 = img2;

      iter++;
    }

    return iter;
  }

  @Override
  public long[] iterationsAt(double[] real, double[] img, long iterationLimit) {
    long[] iterations = new long[real.length];
    for (int i = 0; i < real.length; ++i) {
      iterations[i] = iterationsAt(real[i], img[i], iterationLimit);
    }
    return iterations;
  }
}
