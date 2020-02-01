package com.torrentcome.brow

import sun.jvm.hotspot.opto.Block

data class Canvas(
        var pixels: ArrayList<Color> = ArrayList(),
        var width: Float,
        var height: Float) {

    fun paint_item(item: DisplayCommand) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

typealias DisplayList = ArrayList<DisplayCommand>

abstract class DisplayCommand
data class SolidColor(var color: Color, var rect: Rect) : DisplayCommand()

object Painting {
    /// Paint a tree of LayoutBoxes to an array of pixels.
    fun paint(layout_root: LayoutBox, bounds: Rect): Canvas {
        val display_list = build_display_list(layout_root)
        val canvas = Canvas(width = bounds.width, height = bounds.height)
        for (item in display_list) {
            canvas.paint_item(item)
        }
        return canvas
    }

    private fun build_display_list(layoutRoot: LayoutBox): DisplayList {
        val list = DisplayList()
        render_layout_box(list, layoutRoot)
        return list
    }

    private fun render_layout_box(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        render_background(list, layout_box)
        render_borders(list, layout_box)
        for (child in layout_box.children) {
            render_layout_box(list, child)
        }
    }

    private fun render_background(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        get_color(layout_box, "background")?.let {
            list.add(SolidColor(color = it, rect = layout_box.dimensions.borderBox()))
        }
    }

    private fun render_borders(list: java.util.ArrayList<DisplayCommand>, layout_box: LayoutBox) {
        val color = get_color(layout_box, "border-color") ?: return

        val d = layout_box.dimensions
        val border_box = d.borderBox()

        // Left border
        list.add(SolidColor(color, Rect(
                x = border_box.x,
                y = border_box.y,
                width = d.border.left,
                height = border_box.height
        )))

        // Right border
        list.add(SolidColor(color, Rect(
                x = border_box.x + border_box.width - d.border.right,
                y = border_box.y,
                width = d.border.right,
                height = border_box.height
        )))

        // Top border
        list.add(SolidColor(color, Rect(
                x = border_box.x,
                y = border_box.y,
                width = border_box.width,
                height = d.border.top
        )))

        // Bottom border
        list.add(SolidColor(color, Rect(
                x = border_box.x,
                y = border_box.y + border_box.height - d.border.bottom,
                width = border_box.width,
                height = d.border.bottom
        )))
    }

    private fun get_color(layoutBox: LayoutBox, s: String): Color? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}