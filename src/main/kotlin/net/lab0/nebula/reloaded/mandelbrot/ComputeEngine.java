package net.lab0.nebula.reloaded.mandelbrot;

public interface ComputeEngine {
  /**
   * @param real           The real part coordinate of the point to compute.
   * @param img            The imaginary part coordinate of the point to compute.
   * @param iterationLimit The maximum number of iterations to do.
   * @return The number of iterations. (Can be an approximation)
   */
  long iterationsAt(double real, double img, long iterationLimit);

  /**
   * @param real
   * @param img
   * @param iterationLimit
   * @return
   */
  long[] iterationsAt(double[] real, double[] img, long iterationLimit);
}
