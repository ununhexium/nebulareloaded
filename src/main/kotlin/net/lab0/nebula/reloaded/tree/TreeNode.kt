package net.lab0.nebula.reloaded.tree

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
   * Convenience constructor to build a rectangle based only on bounds.
   * The min/max bound are detected automatically.
   */
  constructor(
      xRange: Pair<Number, Number>,
      yRange: Pair<Number, Number>,
      metadata: MetaData,
      parent: TreeNode? = null
  ) : this(
      RectangleImpl(xRange, yRange),
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
            position.minX to position.midX,
            position.minY to position.midY,
            metadata,
            this
        ),
        TreeNode(
            position.midX to position.maxX,
            position.minY to position.midY,
            metadata,
            this
        ),
        TreeNode(
            position.minX to position.midX,
            position.midY to position.maxY,
            metadata,
            this
        ),
        TreeNode(
            position.midX to position.maxX,
            position.midY to position.maxY,
            metadata,
            this
        )
    )
  }

  fun shouldSplit(): Boolean {
    val (reals, imgs) = getPointsSample()
    val iterations = metadata.computeEngine
        .iterationsAt(reals, imgs, metadata.iterationLimit)

    this.payload.minIterations = iterations.min()!!
    this.payload.maxIterations = iterations.max()!!

    return payload.status == EDGE
  }

  private fun getPointsSample(): Pair<DoubleArray, DoubleArray> {
    val xs = position.listOfX(metadata.edgeSplits)
    val ys = position.listOfY(metadata.edgeSplits)

    val reals = DoubleArray(xs.size * ys.size)
    val imgs = DoubleArray(xs.size * ys.size)

    ys.forEachIndexed { iy, y ->
      xs.forEachIndexed { ix, x ->
        val index = iy * xs.size + ix
        reals[index] = x
        imgs[index] = y
      }
    }

    return reals to imgs
  }

  fun needsCompute() = this.payload.status === UNDEFINED

  fun compute() {
    if (shouldSplit()) split()
  }

  /**
   * @param depthFilter Continue going deeper if this filter is true
   * @param filter Default to accepting all the nodes
   */
  fun getNodesBreadthFirst(
      depthFilter: (self: TreeNode) -> Boolean = { true },
      filter: (self: TreeNode) -> Boolean = { true }
  ): List<TreeNode> {
    val collector = ArrayList<TreeNode>()
    if (filter(this)) collector.add(this)
    if (depthFilter(this)) getNodesBreadthFirst(collector, depthFilter, filter)
    return collector
  }

  private fun getNodesBreadthFirst(
      collector: MutableList<TreeNode>,
      depthFilter: (self: TreeNode) -> Boolean,
      filter: (self: TreeNode) -> Boolean
  ) {
    if (hasChildren()) {
      children!!.forEach {
        if (filter(it)) collector.add(it)
      }
      children!!.forEach {
        if (depthFilter(it)) {
          it.getNodesBreadthFirst(collector, depthFilter, filter)
        }
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

  fun hasChildren() = children != null

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
