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

data class StyledNode(var node: Node, var specifiedValues: PropertyMap, var children: ArrayList<StyledNode>) {
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

object Style {

    /// Apply a stylesheet to an entire DOM tree, returning a StyledNode tree.
    ///
    /// This finds only the specified values at the moment. Eventually it should be extended to find the
    /// computed values too, including inherited values.
    fun styleTree(root: Node, stylesheet: Stylesheet): StyledNode {
        return StyledNode(
            node = root,
            specifiedValues = when (root.nodeType) {
                is NodeType.Element -> specifiedValues(
                    elem = (root.nodeType as NodeType.Element).elementData,
                    stylesheet = stylesheet
                )
                is NodeType.Text -> HashMap()
                else -> throw(Exception())
            },
            children = root.children.map { child -> styleTree(child, stylesheet) } as ArrayList<StyledNode>)
    }

    // Apply styles to a single element, returning the specified styles.
    // To do: Allow multiple UA/author/user stylesheets, and implement the cascade.
    private fun specifiedValues(elem: ElementData, stylesheet: Stylesheet): PropertyMap {
        val values = PropertyMap()
        val rules = matchingRules(elem, stylesheet)

        // Go through the rules from lowest to highest specificity.
        // rules.sortBy { it.selectors }
        // rules.sortBy { i -> i!!.rule.declarations.toString() }

        for (rule in rules) {
            if (rule != null) {
                for (declaration in rule.rule.declarations) {
                    values[declaration.name] = declaration.value
                }
            }
        }
        return values
    }

    private fun matchingRules(elem: ElementData, stylesheet: Stylesheet): ArrayList<MatchedRule?> {
        return stylesheet.rules.map { e -> matchRule(elem, e) } as ArrayList<MatchedRule?>
    }

    private fun matchRule(elem: ElementData, rule: Rule?): MatchedRule? {
        val find: Selector? = rule?.selectors?.find { selector -> matches(elem, selector) }
        return find?.let { MatchedRule(it.specificity(), rule) }
    }

    private fun matches(elem: ElementData, selector: Selector?): Boolean {
        return when (selector) {
            is Simple -> matchesSimpleSelector(elem, selector.simpleSelector)
            else -> false
        }
    }

    private fun matchesSimpleSelector(elem: ElementData, selector: SimpleSelector?): Boolean {
        // Check type selector
        if (selector?.tag_name != null && !selector.tag_name!!.contains(elem.tag_name)) {
            return false
        }

        // Check ID selector
        if (selector?.id != null && !selector.id!!.contains(elem.id().toString())) {
            return false
        }

        // Check class selectors
        val elemClasses: HashSet<String> = elem.classes()
        if (selector?._class != null && selector._class.any { _class -> !elemClasses.contains(_class) }) {
            return false
        }

        // We didn't find any non-matching selector components.
        return true
    }

}