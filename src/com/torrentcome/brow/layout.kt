package com.torrentcome.brow


data class Rect(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f
)

data class EdgeSizes(
    var left: Float = 0f,
    var right: Float = 0f,
    var top: Float = 0f,
    var bottom: Float = 0f
)

data class Dimensions(
    /// Position of the content area relative to the document origin:
    var content: Rect = Rect(),
    // Surrounding edges:
    var padding: EdgeSizes = EdgeSizes(),
    var border: EdgeSizes = EdgeSizes(),
    var margin: EdgeSizes = EdgeSizes()
)

abstract class BoxType
class AnonymousBlock : BoxType()
class InlineNode(var styledNode: StyledNode) : BoxType()
class BlockNode(var styledNode: StyledNode) : BoxType()


/// A node in the layout tree.
data class LayoutBox(
    var dimensions: Dimensions,
    var box_type: BoxType,
    var children: ArrayList<LayoutBox>
) {
    fun new(box_type: BoxType): LayoutBox {
        return LayoutBox(
            box_type = box_type,
            dimensions = Dimensions(),
            children = ArrayList()
        )
    }

    fun get_style_node(): StyledNode {
        return when (box_type) {
            is BlockNode -> (box_type as BlockNode).styledNode
            is InlineNode -> (box_type as InlineNode).styledNode
            is AnonymousBlock -> throw Exception("Anonymous block box has no style node")
            else -> throw Exception()
        }
    }

    fun get_inline_container(): LayoutBox {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

object Layout {
    fun layoutTree(node: StyledNode, containing_block: Dimensions): LayoutBox {
        // The layout algorithm expects the container height to start at 0.
        // TODO: Save the initial containing block height, for calculating percent heights.
        containing_block.content.height = 0.0f

        val rootBox = buildLayoutTree(node)
        rootBox.layout(containing_block)
        return rootBox
    }

    private fun buildLayoutTree(node: StyledNode): LayoutBox {
        // Create the root box.
        val root = LayoutBox(
            dimensions = Dimensions(),
            children = ArrayList(),
            box_type = when (node.display()) {
                Display.Block -> BlockNode(styledNode = node)
                Display.Inline -> InlineNode(styledNode = node)
                Display.None -> throw Exception("Root node has display: none.")
            }
        )

        for (child in node.children) {
            when (child.display()) {
                Display.Block -> root.children.add(buildLayoutTree(child))
                Display.Inline -> root.get_inline_container().children.add(buildLayoutTree(child))
                Display.None -> {
                    // Don't lay out nodes with display: none;
                }
            }
        }

        return root
    }
}

private fun LayoutBox.layout(containing_block: Dimensions) {
    when (this.box_type) {
        is BlockNode -> layout_block(containing_block)
        is InlineNode -> {
        }
        is AnonymousBlock -> {
        }
    }
}

fun LayoutBox.layout_block(containing_block: Dimensions) {
    // Child width can depend on parent width, so we need to calculate this box's width before
    // laying out its children.
    calculate_block_width(containing_block)

    // Determine where the box is located within its container.
    calculate_block_position(containing_block)

    // Recursively lay out the children of this box.
    layout_block_children()

    // Parent height can depend on child height, so `calculate_height` must be called after the
    // children are laid out.
    calculate_block_height()
}

private fun LayoutBox.calculate_block_width(containing_block: Dimensions) {
    val style = get_style_node()

    // `width` has initial value `auto`.
    val auto = Keyword("auto")
    var width = if (style.value("width") == null) auto else style.value("width")

    // margin, border, and padding have initial value 0.
    val zero = Length(0.0f, Px())

    var margin_left = style.lookup("margin-left", "margin", zero)
    var margin_right = style.lookup("margin-right", "margin", zero)

    val border_left = style.lookup("border-left-width", "border-width", zero)
    val border_right = style.lookup("border-right-width", "border-width", zero)

    val padding_left = style.lookup("padding-left", "padding", zero)
    val padding_right = style.lookup("padding-right", "padding", zero)

    val total =
        margin_left.toPx() + margin_right.toPx() + border_left.toPx() + border_right.toPx() + padding_left.toPx() + padding_right.toPx()

    if (width != auto && total > containing_block.content.width) {
        if (margin_left == auto) {
            margin_left = Length(0.0f, Px())
        }
        if (margin_right == auto) {
            margin_right = Length(0.0f, Px())
        }
    }


    // Adjust used values so that the above sum equals `containing_block.width`.
    // Each arm of the `match` should increase the total width by exactly `underflow`,
    // and afterward all values should be absolute lengths in px.
    val underflow = containing_block.content.width - total

    val b = width == auto
    val b1 = margin_left == auto
    val b2 = margin_right == auto

    val bTotal = b.toInt() * 100 + b1.toInt() * 10 + b2.toInt()
    when (bTotal) {
        0 -> margin_right = Length(margin_right.toPx() + underflow, Px())
        1 -> margin_right = Length(underflow, Px())
        10 -> margin_left = Length(underflow, Px())
        11 -> {
            margin_left = Length(underflow / 2.0f, Px())
            margin_right = Length(underflow / 2.0f, Px())
        }
        else -> {
            if (margin_left == auto) {
                margin_left = Length(0.0f, Px()); }
            if (margin_right == auto) {
                margin_right = Length(0.0f, Px()); }

            if (underflow >= 0.0f) {
                // Expand width to fill the underflow.
                width = Length(underflow, Px())
            } else {
                // Width can't be negative. Adjust the right margin instead.
                width = Length(0.0f, Px())
                margin_right = Length(margin_right.toPx() + underflow, Px())
            }
        }
    }
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun LayoutBox.calculate_block_position(containing_block: Dimensions) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun LayoutBox.layout_block_children() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun LayoutBox.calculate_block_height() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun Boolean.toInt() = if (this) 1 else 0