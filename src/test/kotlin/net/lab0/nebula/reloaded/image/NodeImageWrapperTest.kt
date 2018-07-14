package net.lab0.nebula.reloaded.image

import net.lab0.nebula.reloaded.junit.ManualTest
import net.lab0.nebula.reloaded.tree.NodePosition.BOTTOM_RIGHT
import net.lab0.nebula.reloaded.tree.NodePosition.TOP_LEFT
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.nebula.reloaded.tree.TreeNodeTest

class NodeImageWrapperTest {
  @ManualTest
  fun `can render an image`() {
    val node = TreeNodeTest.defaultMandelbrotArea()
    node.compute()
    node[TOP_LEFT].compute()
    node[TOP_LEFT][BOTTOM_RIGHT].compute()

    listOf(256, 512).forEach { width ->
      listOf(256, 512).forEach { height ->
        listOf(0.0, 0.5).forEach { margin ->
          val image = NodeImageWrapper(node, width, height, margin).toImage()
          save(image, "out/test/image/$width-$height-$margin", "png")
        }
      }
    }
  }

  @ManualTest
  fun `can limit the rendering depth`() {
    val node = TreeNodeTest.defaultMandelbrotArea()
    (1..5).forEach {
      node.getNodesBreadthFirst { it.payload.status == UNDEFINED }.forEach {
        it.compute()
      }
    }

    val image = NodeImageWrapper(node, 1024, 1024, 0.1).toImage()
    save(image, "out/test/image/limit_depth", "png")
  }
}