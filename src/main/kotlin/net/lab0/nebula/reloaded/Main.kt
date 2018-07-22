package net.lab0.nebula.reloaded

import net.lab0.nebula.reloaded.compute.mandelbrot.Engines
import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.TreeNode

fun main(args: Array<String>) {
  val metadata = MetaData(256, 2, Engines.Optim2)
  val root = TreeNode(-2 to 2, -2 to 2, metadata)
  if (root.shouldSplit()) {
    root.split()
  }
}
