package com.torrentcome.brow

import java.util.*
import kotlin.collections.HashSet

typealias AttrMap = HashMap<String, String>

data class Node(var children: Vector<Node>, var nodeType: NodeType)

abstract class NodeType {
    data class Element(val elementData: ElementData) : NodeType()
    data class Text(val string: String) : NodeType()
}

data class ElementData(var tag_name: String, var attributes: AttrMap = AttrMap()) {
    fun id(): String? {
        return attributes["id"]
    }

    fun classes(): HashSet<String> {
        val classList: String? = attributes["class"]
        return when (classList) {
            null -> HashSet()
            "" -> HashSet()
            else -> classList.split(' ').toHashSet()
        }
    }
}

fun text(data: String): Node {
    return Node(children = Vector(), nodeType = NodeType.Text(data))
}

fun elem(name: String, attrs: AttrMap, children: Vector<Node>): Node {
    return Node(
            children = children,
            nodeType = NodeType.Element(
                    ElementData(
                            tag_name = name,
                            attributes = attrs
                    )
            )
    )
}