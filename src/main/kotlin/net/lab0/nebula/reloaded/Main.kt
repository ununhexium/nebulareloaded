package net.lab0.nebula.reloaded

import net.lab0.nebula.reloaded.mandelbrot.Optim2
import net.lab0.nebula.reloaded.tree.MetaData
import net.lab0.nebula.reloaded.tree.TreeNode

fun main(args: Array<String>) {
  val metadata = MetaData(256, 2, Optim2)
  val root = TreeNode(-2, -2, 2, 2, metadata)
  if (root.shouldSplit()) {
    root.split()
  }
}
