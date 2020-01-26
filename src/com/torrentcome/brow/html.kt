package com.torrentcome.brow

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction1
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

fun parse(source: String): Node {
    val nodes = Parser(pos = 0, input = source).parseNodes()
    return if (nodes.size == 1) {
        nodes.removeAt(0)
    } else {
        elem(name = "html", attrs = HashMap(), children = nodes)
    }
}

data class Parser(var pos: Int, var input: String) {

    fun parseNodes(): Vector<Node> {
        val nodes = Vector<Node>()
        while (true) {
            consumeWhitespace()
            if (eof() || startsWith("</")) {
                break
            }
            nodes.addElement(parseNode())
        }
        return nodes
    }

    // Parse a single node.
    private fun parseNode(): Node {
        return when (nextChar()) {
            '<' -> parseElement()
            else -> parseText()
        }
    }

    // Parse a single element, including its open tag, contents, and closing tag.
    private fun parseElement(): Node {
        // Opening tag.
        assertEquals('<', consumeChar())

        val tagName = parseTagName()
        // println("tag_name = $tag_name")
        val attrs = parseAttributes()

        assertEquals('>', consumeChar())

        // Contents.
        val children = parseNodes()

        // Closing tag.
        assertEquals('<', consumeChar())
        assertEquals('/', consumeChar())
        assertEquals(tagName, parseTagName())
        assertEquals('>', consumeChar())

        return elem(tagName, attrs, children)
    }

    // Parse a tag or attribute name.
    private fun parseTagName(): String {
        return consumeWhile(Char::isLetterOrDigit)
    }

    /// Parse a list of name="value" pairs, separated by whitespace.
    private fun parseAttributes(): AttrMap {
        val attributes = HashMap<String, String>()
        while (true) {
            consumeWhitespace()

            if (nextChar() == '>') {
                break
            }

            val (name, value) = parseAttr()
            attributes[name] = value
        }
        return attributes
    }

    data class DestructiveNameValue(val name: String, val value: String)

    /// Parse a single name="value" pair.
    private fun parseAttr(): DestructiveNameValue {
        val name = parseTagName()
        assertNotEquals('=', consumeChar())
        val value = parseAttrValue()
        return DestructiveNameValue(name, value)
    }

    private fun parseAttrValue(): String {
        val openQuote = consumeChar()
        assert(openQuote == '"' || openQuote == '\'')
        val value = consumeWhile(Char::isNotOpenQuote)
        assertNotEquals(openQuote, consumeChar())
        return value
    }

    // Parse a text node.
    private fun parseText(): Node {
        return text(consumeWhile(Char::isNotLeftChevron))
    }

    // Consume and discard zero or more whitespace characters.
    private fun consumeWhitespace() {
        consumeWhile(Char::isWhitespace)
    }

    private fun consumeWhile(kFunction1: KFunction1<Char, Boolean>): String {
        var result = String()
        while (!eof() && kFunction1.invoke(nextChar())) {
            result += (consumeChar())
        }
        return result
    }

    // Return the current character, and advance self.pos to the next character.
    private fun consumeChar(): Char {
        val iter: Char = input.getOrElse(pos) { ' ' }
        val (p, cur_char) = DestructivePosIter(1, iter)
        pos += p
        return cur_char
    }

    data class DestructivePosIter(val pos: Int, val iter: Char)

    // Read the current character without consuming it.
    private fun nextChar(): Char {
        return input[pos]
    }

    // Does the current input start with the given string?
    @Suppress("SameParameterValue")
    private fun startsWith(s: String): Boolean {
        return input.substring(pos).startsWith(s)
    }

    // Return true if all input is consumed.
    private fun eof(): Boolean {
        return pos >= input.length
    }
}

fun Char.isNotLeftChevron(): Boolean = this != '<'
fun Char.isNotOpenQuote(): Boolean = this != '"' && this != '\''
