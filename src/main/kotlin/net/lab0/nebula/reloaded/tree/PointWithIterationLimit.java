package net.lab0.nebula.reloaded.tree;

public class PointWithIterationLimit {
  public PointWithIterationLimit(double x, double y, long iterationLimit) {
    this.x = x;
    this.y = y;
    this.iterationLimit = iterationLimit;
  }

  public double x;
  public double y;
  public long iterationLimit;
}
