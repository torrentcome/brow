package com.torrentcome.brow

import java.util.*

typealias PropertyMap = HashMap<String, Value?>

/// A single CSS rule and the specificity of its most specific matching selector.
data class MatchedRule(var specificity: Specificity, var rule: Rule)

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

class Style {

    // Apply styles to a single element, returning the specified styles.
    // To do: Allow multiple UA/author/user stylesheets, and implement the cascade.
    fun specifiedValues(elem: ElementData, stylesheet: Stylesheet) : PropertyMap {
        val values = PropertyMap()
        val rules = matching_rules(elem, stylesheet)

        // Go through the rules from lowest to highest specificity.
        // rules.sortBy { it.selectors }
        for (rule in rules) {
            for (declaration in rule.declarations) {
                values[declaration.name] = declaration.value
            }
        }
        return values
    }

    private fun matching_rules(elem: ElementData, stylesheet: Stylesheet): Vector<Rule> {
        return Vector()
    }
}