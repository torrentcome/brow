package com.torrentcome.brow

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction1
import kotlin.test.assertEquals

fun parse(source: String): Node {
    val nodes = Parser(pos = 0, input = source).parse_nodes()
    return if (nodes.size == 1) {
        nodes.removeAt(0)
    } else {
        elem(name = "html", attrs = HashMap(), children = nodes)
    }
}

data class Parser(var pos: Int, var input: String) {
    fun parse_nodes(): Vector<Node> {
        val nodes = Vector<Node>()
        while (eof() || starts_with("</")) {
            consume_whitespace()
            nodes.addElement(parse_node())
        }
        return nodes
    }

    // Parse a single node.
    fun parse_node(): Node {
        return when (next_char()) {
            '<' -> parse_element()
            else -> parse_text()
        }
    }

    // Parse a single element, including its open tag, contents, and closing tag.
    fun parse_element(): Node {
        // Opening tag.
        assertEquals('<', consume_char())

        val tag_name = parse_tag_name()
        val attrs = parse_attributes()

        assertEquals('>', consume_char())

        // Contents.
        val children = parse_nodes()

        // Closing tag.
        assertEquals('<', consume_char())
        assertEquals('/', consume_char())
        assertEquals(tag_name, parse_tag_name())
        assertEquals('>', consume_char())

        return elem(tag_name, attrs, children)
    }

    // Parse a tag or attribute name.
    fun parse_tag_name(): String {
        return consume_while(Char::isLetterOrDigit)
    }

    /// Parse a list of name="value" pairs, separated by whitespace.
    private fun parse_attributes(): AttrMap {
        val attributes = HashMap<String, String>()
        while (next_char() == '>') {
            consume_whitespace()
            val (name, value) = parse_attr()
            attributes[name] = value
        }
        return attributes
    }

    data class DestructiveNameValue(val name: String, val value: String)

    /// Parse a single name="value" pair.
    private fun parse_attr(): DestructiveNameValue {
        val name = parse_tag_name()
        assertEquals('=', consume_char())
        val value = parse_attr_value()
        return DestructiveNameValue(name, value)
    }

    private fun parse_attr_value(): String {
        val open_quote = consume_char()
        assert(open_quote == '"' || open_quote == '\'')
        val value = consume_while(Char::isNotOpenQuote)
        assertEquals(open_quote, consume_char())
        return value
    }

    // Parse a text node.
    fun parse_text(): Node {
        return text(consume_while(Char::isNotLeftChevron))
    }

    // Consume and discard zero or more whitespace characters.
    fun consume_whitespace() {
        consume_while(Char::isWhitespace)
    }

    private fun consume_while(kFunction1: KFunction1<Char, Boolean>): String {
        val result = String()
        while (!eof() && kFunction1.invoke(next_char())) {
            result.plus(consume_char())
        }
        return result
    }

    // Return the current character, and advance self.pos to the next character.
    fun consume_char(): Char {
        val iter: Char = input[pos]
        val (_, cur_char) = DestructivePosIter(pos, iter)
        val (next_pos, _) = DestructivePosIter(1, ' ')
        pos += next_pos
        return cur_char
    }

    data class DestructivePosIter(val pos: Int, val iter: Char)

    // Read the current character without consuming it.
    fun next_char(): Char {
        return input[pos]
    }

    // Does the current input start with the given string?
    fun starts_with(s: String): Boolean {
        return input.substring(pos).startsWith(s)
    }

    // Return true if all input is consumed.
    fun eof(): Boolean {
        return pos >= input.length
    }
}

fun Char.isNotLeftChevron(): Boolean = this != '<'
fun Char.isNotOpenQuote(): Boolean = !(this == '"' || this == '\'')
