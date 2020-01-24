import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

typealias AttrMap = HashMap<String, String>

class Node(val children: Vector<Node>, nodeType: NodeType)

abstract class NodeType {
    class Element(val elementData: ElementData) : NodeType()
    class Text(val string: String) : NodeType()
}


class ElementData(tag_name: String, var attributes: AttrMap) {
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

fun elemen(name: String, attrs: AttrMap, children: Vector<Node>): Node {
    return Node(
        children = children,
        nodeType = NodeType.Element(ElementData(tag_name = name, attributes = attrs))
    )
}