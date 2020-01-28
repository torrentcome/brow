package com.torrentcome.brow

import java.util.*

typealias PropertyMap = HashMap<String, Value?>

enum class Display {
    Inline,
    Block,
    None
}

data class StyleNode<Any>(var node: Node, var specifiedValues: PropertyMap, var children: Vector<StyleNode<Any>>) {
    // Return the specified value of a property if it exists, otherwise `None`.
    fun value(name: String): Value? {
        return specifiedValues[name]
    }

    /// Return the specified value of property `name`, or property `fallback_name` if that doesn't
    /// exist, or value `default` if neither does.
    fun lookup(name: String, fallback_name: String, default: Value): Value {
        var value: Value? = value(name)
        if (value == null) value = value(fallback_name)
        if (value == null) value = default
        return value
    }

    /// The value of the `display` property (defaults to inline).
    fun display(): Display {
        return when (value("display")) {
            is Keyword -> {
                val keyword = value("display") as Keyword
                when (keyword.string) {
                    "block" -> Display.Block
                    "none" -> Display.None
                    else -> Display.Inline
                }
            }
            else -> Display.Inline
        }
    }
}
