package com.torrentcome.brow

import java.util.*
import kotlin.collections.HashMap

typealias PropertyMap = HashMap<String, Value?>

/// A single CSS rule and the specificity of its most specific matching selector.
data class MatchedRule(var specificity: Specificity, var rule: Rule)

enum class Display {
    Inline,
    Block,
    None
}

data class StyledNode(var node: Node, var specifiedValues: PropertyMap, var children: Vector<StyledNode>) {
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

    /// Apply a stylesheet to an entire DOM tree, returning a StyledNode tree.
    ///
    /// This finds only the specified values at the moment. Eventually it should be extended to find the
    /// computed values too, including inherited values.
    fun styleTree(root: Node, stylesheet: Stylesheet): StyledNode {
        return StyledNode(
                node = root,
                specifiedValues = when (root.nodeType) {
                    is NodeType.Element -> specifiedValues(elem = (root.nodeType as NodeType.Element).elementData, stylesheet = stylesheet)
                    is NodeType.Text -> HashMap()
                    else -> throw(Exception())
                },
                children = root.children.map { child -> styleTree(child, stylesheet) } as Vector<StyledNode>)
    }

    // Apply styles to a single element, returning the specified styles.
    // To do: Allow multiple UA/author/user stylesheets, and implement the cascade.
    fun specifiedValues(elem: ElementData, stylesheet: Stylesheet): PropertyMap {
        val values = PropertyMap()
        val rules = matching_rules(elem, stylesheet)

        // Go through the rules from lowest to highest specificity.
        // rules.sortBy { it.selectors }
        rules.sortBy { i -> i!!.rule.declarations.toString() }
        for (rule in rules) {
            for (declaration in rule!!.rule.declarations) {
                values[declaration.name] = declaration.value
            }
        }
        return values
    }

    fun matching_rules(elem: ElementData, stylesheet: Stylesheet): Vector<MatchedRule?> {
        val filter: List<MatchedRule?> = stylesheet.rules.map { e -> match_rule(elem, e) }
        return filter as Vector<MatchedRule?>
    }

    private fun match_rule(elem: ElementData, rule: Rule?): MatchedRule? {
        val find: Selector? = rule?.selectors?.find { selector -> matches(elem, selector) }
        return find?.let { MatchedRule(it.specificity(), rule) }
    }

    private fun matches(elem: ElementData, selector: Selector?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}