package net.lab0.nebula.reloaded.compute.nebulabrot

import net.lab0.nebula.reloaded.tree.Point

interface NebulabrotComputeEngine {
  fun compute(points: Iterator<Point>, output: RenderingSurface)
}
