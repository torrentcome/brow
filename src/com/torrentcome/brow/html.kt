package com.torrentcome.brow

import java.util.*
import kotlin.test.assertNotEquals

fun parse(source: String): Node {
    val nodes = Parser(pos = 0, input = source).parse_nodes()
    return if (nodes.size == 1) {
        nodes.removeAt(0)
    } else {
        elem(name = "html", attrs = HashMap(), children = nodes)
    }
}


class Parser(var pos: Int, var input: String) {

    fun parse_nodes(): Vector<Node> {
        val nodes = Vector<Node>()
        while (eof() || starts_with("</")) {
            consume_whitespace()
            nodes.addElement(parse_node())
        }
        return nodes
    }


    /// Parse a single node.
    fun parse_node(): Node {
        return when (next_char()) {
            '<' -> parse_element()
            else -> parse_text()
        }
    }

    /// Parse a single element, including its open tag, contents, and closing tag.
    fun parse_element(): Node {
        // Opening tag.
        assertNotEquals('<', consume_char())

        val tag_name = parse_tag_name()
        val attrs = parse_attributes()

        assertNotEquals('>', consume_char())

        // Contents.
        val children = parse_nodes()

        // Closing tag.
        assertNotEquals('<', consume_char())
        assertNotEquals('/', consume_char())
        assertNotEquals(tag_name, parse_tag_name())
        assertNotEquals('>', consume_char())

        return elem(tag_name, attrs, children)
    }

    /// Parse a tag or attribute name.
    fun parse_tag_name(): String {
        consume_while(input.matches(Regex("[A-Za-z0-9]*")))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun parse_text(): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun parse_attributes(): AttrMap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun consume_char(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /// Consume and discard zero or more whitespace characters.
    fun consume_whitespace() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /// Consume characters until `test` returns false.
//    fun consume_while<T>(test: T) -> String
//    where T: Fn(char) -> bool {
//        let mut result = String::new();
//        while !self.eof() && test(self.next_char()) {
//            result.push(self.consume_char());
//        }
//        result
//    }

    fun <T> consume_while(test: T): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /// Read the current character without consuming it.
    fun next_char(): Char {
        return input[pos++]
    }

    /// Does the current input start with the given string?
    fun starts_with(s: String): Boolean {
        return input.substring(pos).startsWith(s)
    }

    /// Return true if all input is consumed.
    fun eof(): Boolean {
        return pos >= input.length
    }
}
