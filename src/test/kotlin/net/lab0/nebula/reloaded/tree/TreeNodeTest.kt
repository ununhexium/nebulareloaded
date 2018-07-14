package net.lab0.nebula.reloaded.tree

import net.lab0.nebula.reloaded.mandelbrot.Optim2
import net.lab0.nebula.reloaded.tree.NodePosition.BOTTOM_LEFT
import net.lab0.nebula.reloaded.tree.NodePosition.BOTTOM_RIGHT
import net.lab0.nebula.reloaded.tree.NodePosition.ROOT
import net.lab0.nebula.reloaded.tree.NodePosition.TOP_LEFT
import net.lab0.nebula.reloaded.tree.NodePosition.TOP_RIGHT
import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.INSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.OUTSIDE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TreeNodeTest {

  companion object {
    val metadata = MetaData(128, 8, Optim2)

    fun defaultMandelbrotArea() =
        TreeNode(-2 to 2, -2 to 2, metadata)
  }

  @Test
  fun `split the node in 4 equal parts`() {
    val node = TreeNode(0 to 1, 0 to 1, metadata)
    node.split()

    assertThat(node.children).hasSize(4)

    assertThat(node.children!![0].position)
        .isEqualTo(RectangleImpl(0 to 0.5, 0 to 0.5))

    assertThat(node.children!![1].position)
        .isEqualTo(RectangleImpl(0.5 to 1, 0 to 0.5))

    assertThat(node.children!![2].position)
        .isEqualTo(RectangleImpl(0 to 0.5, 0.5 to 1))

    assertThat(node.children!![3].position)
        .isEqualTo(RectangleImpl(0.5 to 1, 0.5 to 1))

    node.children!!.forEach {
      assertThat(it.parent).isNotNull

      node[2].split()
      assertThat(node[2][0].position)
          .isEqualTo(RectangleImpl(0 to 0.25, 0.5 to 0.75))
    }
  }

  @Test
  fun `split the node in 4 equal parts in negative values`() {
    val node = TreeNode(-1 to 0, -1 to 0, metadata)
    node.split()

    assertThat(node.children).hasSize(4)

    assertThat(node.children!![0].position)
        .isEqualTo(RectangleImpl(-1 to -0.5, -1 to -0.5))

    assertThat(node.children!![1].position)
        .isEqualTo(RectangleImpl(-0.5 to 0, -1 to -0.5))

    assertThat(node.children!![2].position)
        .isEqualTo(RectangleImpl(-1 to -0.5, -0.5 to 0))

    assertThat(node.children!![3].position)
        .isEqualTo(RectangleImpl(-0.5 to 0, -0.5 to 0))

    node.children!!.forEach {
      assertThat(it.parent).isNotNull

      node[2].split()
      assertThat(node[2][0].position)
          .isEqualTo(RectangleImpl(-1 to -0.75, -0.5 to -0.25))
    }
  }

  @Test
  fun `at creation, a node doesn't have a status`() {
    assertThat(
        TreeNode(0 to 1, 0 to 1, metadata).payload.status
    ).isEqualTo(UNDEFINED)
  }

  @Test
  fun `should not split the node if it is fully inside the set`() {
    val node = TreeNode(-0.25 to 0, 0 to 0.25, metadata)
    assertThat(node.shouldSplit()).isFalse()
    assertThat(node.payload.status).isEqualTo(INSIDE)
  }

  @Test
  fun `should not split the node if it is fully outside the set`() {
    val node = TreeNode(10 to 11, 10 to 11, metadata)
    assertThat(node.shouldSplit()).isFalse()
    assertThat(node.payload.status).isEqualTo(OUTSIDE)
  }

  @Test
  fun `should split the node if it crosses the edge of the set`() {
    val node = TreeNode(0 to 1, 0 to 1, metadata)
    assertThat(node.shouldSplit()).isTrue()
    assertThat(node.payload.status).isEqualTo(EDGE)
  }

  @Test
  fun `can compute a node`() {
    val node = defaultMandelbrotArea()
    assertThat(node.needsCompute()).isTrue()
    node.compute()
    assertThat(node.needsCompute()).isFalse()
    assertThat(node.children).isNotEmpty
  }

  @Test
  fun `a node can tell its path`() {
    val node = defaultMandelbrotArea()
    assertThat(node.parent).isNull()
    assertThat(node.path).isEqualTo(listOf(ROOT))
    node.compute()
    assertThat(node[0].path).isEqualTo(listOf(ROOT, BOTTOM_LEFT))
    assertThat(node[1].path).isEqualTo(listOf(ROOT, BOTTOM_RIGHT))
    assertThat(node[2].path).isEqualTo(listOf(ROOT, TOP_LEFT))
    assertThat(node[3].path).isEqualTo(listOf(ROOT, TOP_RIGHT))

    node[NodePosition.TOP_RIGHT].compute()
  }

  @Test
  fun `can get all the nodes`() {
    val node = defaultMandelbrotArea()
    node.compute()
    node[BOTTOM_LEFT].compute()
    node[BOTTOM_LEFT][TOP_RIGHT].compute()

    val leaves = node.getNodesBreadthFirst {
      it.payload.status in PayloadStatus.values()
    }

    val expected = listOf(
        node,

        node[0],
        node[1],
        node[2],
        node[3],

        node[BOTTOM_LEFT][0],
        node[BOTTOM_LEFT][1],
        node[BOTTOM_LEFT][2],
        node[BOTTOM_LEFT][3],

        node[BOTTOM_LEFT][TOP_RIGHT][0],
        node[BOTTOM_LEFT][TOP_RIGHT][1],
        node[BOTTOM_LEFT][TOP_RIGHT][2],
        node[BOTTOM_LEFT][TOP_RIGHT][3]
    )

    assertThat(leaves).isEqualTo(expected)
  }

  @Test
  fun `can get all leaves`() {
    val node = defaultMandelbrotArea()
    node.compute()
    node[BOTTOM_LEFT].compute()

    val leaves = node.getNodesBreadthFirst { !it.hasChildren() }
    val expected = listOf(

        node[BOTTOM_RIGHT],
        node[TOP_LEFT],
        node[TOP_RIGHT],

        node[BOTTOM_LEFT][0],
        node[BOTTOM_LEFT][1],
        node[BOTTOM_LEFT][2],
        node[BOTTOM_LEFT][3]
    )
    assertThat(leaves).isEqualTo(expected)
  }

  @Test
  fun `can get all branches (non leaf)`() {
    val node = defaultMandelbrotArea()
    node.compute()
    node[BOTTOM_LEFT].compute()

    val leaves = node.getNodesBreadthFirst { it.hasChildren() }
    val expected = listOf(
        node,
        node[BOTTOM_LEFT]
    )
    assertThat(leaves).isEqualTo(expected)
  }
}