package com.torrentcome.brow


data class Rect(
        var x: Float = 0f,
        var y: Float = 0f,
        var width: Float = 0f,
        var height: Float = 0f
) {
    fun expandedBy(edge: EdgeSizes): Rect {
        return Rect(
                x = x - edge.left,
                y = y - edge.top,
                width = width + edge.left + edge.right,
                height = height + edge.top + edge.bottom
        )
    }
}

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
) {
    fun marginBox(): Rect {
        return borderBox().expandedBy(margin)
    }

    private fun borderBox(): Rect {
       return paddingBox().expandedBy(border)
    }

    private fun paddingBox(): Rect {
        return content.expandedBy(padding)
    }
}

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

    fun getStyleNode(): StyledNode {
        return when (box_type) {
            is BlockNode -> (box_type as BlockNode).styledNode
            is InlineNode -> (box_type as InlineNode).styledNode
            is AnonymousBlock -> throw Exception("Anonymous block box has no style node")
            else -> throw Exception()
        }
    }

    fun getInlineContainer(): LayoutBox {
        return when (box_type) {
            is InlineNode -> this
            is AnonymousBlock -> this
            is BlockNode -> {
                // If we've just generated an anonymous block box, keep using it.
                // Otherwise, create a new one.
                if (children.isNotEmpty() && children.last().box_type is AnonymousBlock) { }
                else children.add(LayoutBox(Dimensions(), AnonymousBlock(), ArrayList()))
                children.last()
            }
            else -> this
        }
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
                Display.Inline -> root.getInlineContainer().children.add(buildLayoutTree(child))
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
        is BlockNode -> layoutBlock(containing_block)
        is InlineNode -> {
        }
        is AnonymousBlock -> {
        }
    }
}

fun LayoutBox.layoutBlock(containing_block: Dimensions) {
    // Child width can depend on parent width, so we need to calculate this box's width before
    // laying out its children.
    calculateBlockWidth(containing_block)

    // Determine where the box is located within its container.
    calculateBlockPosition(containing_block)

    // Recursively lay out the children of this box.
    layoutBlockChildren()

    // Parent height can depend on child height, so `calculate_height` must be called after the
    // children are laid out.
    calculateBlockHeight()
}

private fun LayoutBox.calculateBlockWidth(containing_block: Dimensions) {
    val style = getStyleNode()

    // `width` has initial value `auto`.
    val auto = Keyword("auto")
    var width = if (style.value("width") == null) auto else style.value("width")

    // margin, border, and padding have initial value 0.
    val zero = Length(0.0f, Px())

    var marginLeft = style.lookup("margin-left", "margin", zero)
    var marginRight = style.lookup("margin-right", "margin", zero)

    val borderLeft = style.lookup("border-left-width", "border-width", zero)
    val borderRight = style.lookup("border-right-width", "border-width", zero)

    val paddingLeft = style.lookup("padding-left", "padding", zero)
    val paddingRight = style.lookup("padding-right", "padding", zero)

    val total =
            marginLeft.toPx() + marginRight.toPx() + borderLeft.toPx() + borderRight.toPx() + paddingLeft.toPx() + paddingRight.toPx()

    if (width != auto && total > containing_block.content.width) {
        if (marginLeft == auto) {
            marginLeft = Length(0.0f, Px())
        }
        if (marginRight == auto) {
            marginRight = Length(0.0f, Px())
        }
    }


    // Adjust used values so that the above sum equals `containing_block.width`.
    // Each arm of the `match` should increase the total width by exactly `underflow`,
    // and afterward all values should be absolute lengths in px.
    val underflow = containing_block.content.width - total

    val b = width == auto
    val b1 = marginLeft == auto
    val b2 = marginRight == auto

    when (b.toInt() * 100 + b1.toInt() * 10 + b2.toInt()) {
        0 -> marginRight = Length(marginRight.toPx() + underflow, Px())
        1 -> marginRight = Length(underflow, Px())
        10 -> marginLeft = Length(underflow, Px())
        11 -> {
            marginLeft = Length(underflow / 2.0f, Px())
            marginRight = Length(underflow / 2.0f, Px())
        }
        // 100 | 101 | 111
        else -> {
            if (marginLeft == auto) {
                marginLeft = Length(0.0f, Px()); }
            if (marginRight == auto) {
                marginRight = Length(0.0f, Px()); }

            if (underflow >= 0.0f) {
                // Expand width to fill the underflow.
                width = Length(underflow, Px())
            } else {
                // Width can't be negative. Adjust the right margin instead.
                width = Length(0.0f, Px())
                marginRight = Length(marginRight.toPx() + underflow, Px())
            }
        }
    }

    val d = dimensions
    d.content.width = width!!.toPx()

    d.padding.left = paddingLeft.toPx()
    d.padding.right = paddingRight.toPx()

    d.border.left = borderLeft.toPx()
    d.border.right = borderRight.toPx()

    d.margin.left = marginLeft.toPx()
    d.margin.right = marginRight.toPx()
}

private fun LayoutBox.calculateBlockPosition(containing_block: Dimensions) {
    val style = getStyleNode()
    val d = dimensions

    // margin, border, and padding have initial value 0.
    val zero = Length(0.0f, Px())

    // If margin-top or margin-bottom is `auto`, the used value is zero.
    d.margin.top = style.lookup("margin-top", "margin", zero).toPx()
    d.margin.bottom = style.lookup("margin-bottom", "margin", zero).toPx()

    d.border.top = style.lookup("border-top-width", "border-width", zero).toPx()
    d.border.bottom = style.lookup("border-bottom-width", "border-width", zero).toPx()

    d.padding.top = style.lookup("padding-top", "padding", zero).toPx()
    d.padding.bottom = style.lookup("padding-bottom", "padding", zero).toPx()

    d.content.x = containing_block.content.x + d.margin.left + d.border.left + d.padding.left

    // Position the box below all the previous boxes in the container.
    d.content.y = containing_block.content.height + containing_block.content.y + d.margin.top + d.border.top + d.padding.top
}

private fun LayoutBox.layoutBlockChildren() {
    val d = dimensions
    for (child in children) {
        child.layout(d)
        // Increment the height so each child is laid out below the previous one.
        d.content.height = d.content.height + child.dimensions.marginBox().height
    }
}

private fun LayoutBox.calculateBlockHeight() {
    // If the height is set to an explicit length, use that exact length.
    // Otherwise, just keep the value set by `layout_block_children`.
    val value = this.getStyleNode().value("height")
    if (value != null)
        this.dimensions.content.height = value.toPx()
}

fun Boolean.toInt() = if (this) 1 else 0