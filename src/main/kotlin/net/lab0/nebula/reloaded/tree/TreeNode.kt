package net.lab0.nebula.reloaded.tree

import com.google.common.collect.Lists
import net.lab0.nebula.reloaded.max
import net.lab0.nebula.reloaded.min
import net.lab0.nebula.reloaded.toDouble
import net.lab0.nebula.reloaded.tree.PayloadStatus.EDGE
import net.lab0.nebula.reloaded.tree.PayloadStatus.UNDEFINED
import net.lab0.tools.delegated.NullableSetOnce

/**
 * Custom quad tree designed to subdivide the Mandelbrot set.
 * Each node must know the minimum and maximum number of iterations required in its area.
 * The iteration counts are estimates and may change if any discrepancy is found.
 *
 * @param minX: lower X bound, included
 * @param maxX: upper X bound, excluded
 * @param minY: lower Y bound, included
 * @param maxY: upper Y bound, excluded
 */
class TreeNode(
    val position: Rectangle,
    val metadata: MetaData,
    val parent: TreeNode? = null
) {
  /**
   * Convenience constructor to be able to use any kind of number as boundaries.
   */
  constructor(
      minX: Number,
      maxX: Number,
      minY: Number,
      maxY: Number,
      metadata: MetaData,
      parent: TreeNode? = null
  ) : this(
      RectangleImpl(
          minX, maxX, minY, maxY
      ),
      metadata,
      parent
  )

  /**
   * Convenience constructor to build a rectangle based only on bounds.
   * The min/max bound are detected automatically.
   */
  constructor(
      xRange: Pair<Number, Number>,
      yRange: Pair<Number, Number>,
      metadata: MetaData,
      parent: TreeNode? = null
  ) : this(
      RectangleImpl(
          xRange.toDouble().min(),
          xRange.toDouble().max(),
          yRange.toDouble().min(),
          yRange.toDouble().max()
      ),
      metadata,
      parent
  )

  val payload = IterationPayload(metadata.iterationLimit)
  var children: Array<TreeNode>? by NullableSetOnce()
  val path: List<NodePosition>
    get() = if (parent == null) {
      listOf(NodePosition.ROOT)
    }
    else {
      parent.path + NodePosition.values()[parent.children!!.indexOf(this)]
    }

  /**
   * Given a parent node P
   * ```
   * +---+
   * |   |
   * | P |
   * |   |
   * +---+
   * ```
   *
   * children node will split the parent node in 4 rectangles
   * of equals size and sames aspect ratio as the parent node.
   * There will be 4 children and the following diagram shows their position in the parent node.
   * ```
   * +-+-+
   * |2|3|
   * +-+-|
   * |0|1|
   * +-+-+
   * ```
   *
   */
  fun split() {
    children = arrayOf(
        TreeNode(
            position.minX,
            position.midX,
            position.minY,
            position.midY,
            metadata,
            this
        ),
        TreeNode(
            position.midX,
            position.maxX,
            position.minY,
            position.midY,
            metadata,
            this
        ),
        TreeNode(
            position.minX,
            position.midX,
            position.midY,
            position.maxY,
            metadata,
            this
        ),
        TreeNode(
            position.midX,
            position.maxX,
            position.midY,
            position.maxY,
            metadata,
            this
        )
    )
  }

  fun shouldSplit(): Boolean {
    val pointsSample = getPointsSample()
    val iterations = pointsSample.map {
      metadata.computeEngine.iterationsAt(it.x, it.y, metadata.iterationLimit)
    }

    this.payload.minIterations = iterations.min()!!
    this.payload.maxIterations = iterations.max()!!

    return payload.status == EDGE
  }

  private fun getPointsSample(): List<Point> {
    return Lists.cartesianProduct(
        position.listOfX(metadata.edgeSplits),
        position.listOfY(metadata.edgeSplits)
    ).map {
      Point(it[0], it[1])
    }
  }

  fun needsCompute() = this.payload.status === UNDEFINED

  fun compute() {
    if (shouldSplit()) split()
  }

  fun getNodesBreadthFirst(filter: (self: TreeNode) -> Boolean): List<TreeNode> {
    val collector = ArrayList<TreeNode>()
    if (filter(this)) collector.add(this)
    getNodesBreadthFirst(collector, filter)
    return collector
  }

  private fun getNodesBreadthFirst(
      collector: MutableList<TreeNode>,
      filter: (self: TreeNode) -> Boolean
  ) {
    if (hasChildren()) {
      children!!.forEach {
        if (filter(this)) collector.add(it)
      }
      children!!.forEach {
        it.getNodesBreadthFirst(collector, filter)
      }
    }
  }

//  fun getNodesDepthFirst(filter: (self: TreeNode) -> Boolean): List<TreeNode> {
//    val self = if (filter(this)) listOf(this) else listOf()
//
//    val children = if (hasChildren()) {
//      children!!.flatMap {
//        it.getNodesBreadthFirst(filter)
//      }
//    }
//    else listOf()
//
//    return self + children
//  }

  private fun hasChildren() = children != null

  /**
   * Unsafe get children at provided index.
   * @return this child in any
   * @throws NullPointerException if there is no child
   */
  operator fun get(index: Int) = children!![index]

  operator fun get(position: NodePosition) = children!![position.ordinal]

  override fun toString() =
      this.path.joinToString(separator = ".") { it.ordinal.toString() }
}
