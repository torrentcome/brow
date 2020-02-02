package com.torrentcome.brow

data class Canvas(
        var pixels: Array<Color>,
        var width: Float,
        var height: Float) {

    fun paintItem(item: DisplayCommand) {
        when (item) {
            is SolidColor -> {
                val rect = item.rect
                val color = item.color
                // Clip the rectangle to the canvas boundaries.
                val x0: Int = rect.x.coerceIn(0.0F, width).toInt()
                val y0: Int = rect.y.coerceIn(0.0F, height).toInt()
                val x1: Int = (rect.x + rect.width).coerceIn(0.0F, width).toInt()
                val y1: Int = (rect.y + rect.height).coerceIn(0.0F, height).toInt()

                for (y in y0..y1) {
                    for (x in x0..x1) {
                        // TODO: alpha compositing with existing pixel
                        pixels[y * width.toInt() + x] = color
                    }
                }
            }
        }
    }
}

fun new(width: Float, height: Float): Canvas {
    val white = Color(r = 255, g = 255, b = 255, a = 255)
    return Canvas(
            pixels = Array(width.toInt() * height.toInt()) { white },
            width = width,
            height = height
    )
}

typealias DisplayList = ArrayList<DisplayCommand>

abstract class DisplayCommand
data class SolidColor(var color: Color, var rect: Rect) : DisplayCommand()

object Painting {
    /// Paint a tree of LayoutBoxes to an array of pixels.
    fun paint(layout_root: LayoutBox, bounds: Rect): Canvas {
        val displayList = buildDisplayList(layout_root)
        val canvas = new(width = bounds.width, height = bounds.height)
        for (item in displayList) {
            canvas.paintItem(item)
        }
        return canvas
    }

    private fun buildDisplayList(layoutRoot: LayoutBox): DisplayList {
        val list = DisplayList()
        renderLayoutBox(list, layoutRoot)
        return list
    }

    private fun renderLayoutBox(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        renderBackground(list, layout_box)
        renderBorders(list, layout_box)
        for (child in layout_box.children) {
            renderLayoutBox(list, child)
        }
    }

    private fun renderBackground(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        getColor(layout_box, "background")?.let {
            list.add(SolidColor(color = it, rect = layout_box.dimensions.borderBox()))
        }
    }

    private fun renderBorders(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        val color = getColor(layout_box, "border-color") ?: return

        val d = layout_box.dimensions
        val borderBox = d.borderBox()

        // Left border
        list.add(SolidColor(color, Rect(
                x = borderBox.x,
                y = borderBox.y,
                width = d.border.left,
                height = borderBox.height
        )))

        // Right border
        list.add(SolidColor(color, Rect(
                x = borderBox.x + borderBox.width - d.border.right,
                y = borderBox.y,
                width = d.border.right,
                height = borderBox.height
        )))

        // Top border
        list.add(SolidColor(color, Rect(
                x = borderBox.x,
                y = borderBox.y,
                width = borderBox.width,
                height = d.border.top
        )))

        // Bottom border
        list.add(SolidColor(color, Rect(
                x = borderBox.x,
                y = borderBox.y + borderBox.height - d.border.bottom,
                width = borderBox.width,
                height = d.border.bottom
        )))
    }

    private fun getColor(layoutBox: LayoutBox, name: String): Color? {
        return when (layoutBox.box_type) {
            is BlockNode -> {
                val value: Value? = (layoutBox.box_type as BlockNode).styledNode.value(name)
                if (value is ColorValue) value.color
                else null
            }
            is InlineNode -> {
                val value: Value? = (layoutBox.box_type as InlineNode).styledNode.value(name)
                if (value is ColorValue) value.color
                else null
            }
            is AnonymousBlock -> null
            else -> null
        }
    }

}