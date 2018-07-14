package net.lab0.nebula.reloaded.mandelbrot;

public interface Compute {
  /**
   * @param real    The real part coordinate of the point to compute.
   * @param img     The imaginary part coordinate of the point to compute.
   * @param maxIter The maximum number of iterations to do.
   * @return The number of iterations. (Can be an approximation)
   */
  long iterationsAt(double real, double img, long maxIter);
}
