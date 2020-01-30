package com.torrentcome.brow

import java.util.*
import kotlin.reflect.KFunction1
import kotlin.test.assertEquals

data class Stylesheet(var rules: Vector<Rule>)

data class Rule(var selectors: Vector<Selector>, var declarations: Vector<Declaration>)

abstract class Selector {
    abstract fun specificity(): Specificity
}

data class Simple(var simpleSelector: SimpleSelector) : Selector() {
    override fun specificity(): Specificity {
        // http://www.w3.org/TR/selectors/#specificity
        val a: Int = simpleSelector.id?.length ?: 0
        val b = simpleSelector._class.size
        val c = simpleSelector.tag_name?.length ?: 0
        return Specificity(a, b, c)
    }
}

data class SimpleSelector(var tag_name: String?, var id: String?, var _class: Vector<String>)

data class Declaration(var name: String, var value: Value)

abstract class Value {
    /// Return the size of a length in px, or zero for non-lengths.
    fun toPx(value: Value?): Float {
        return when (value) {
            is Length -> value.f32
            else -> 0.0f
        }
    }
}

data class Keyword(var string: String) : Value()
data class Length(var f32: Float, var unit: Unit) : Value()
data class ColorValue(var color: Color) : Value()

data class Color(
    var r: Long = -1,
    var g: Long = -1,
    var b: Long = -1,
    var a: Long = -1
)

class Copy(r: Long, g: Long, b: Long, a: Long)

data class Specificity(var a: Int, var b: Int, var c: Int)

abstract class Unit
data class Px(var param : Float = -1f) : Unit()

/// Parse a whole CSS stylesheet.
object Css {

    fun parse(source: String): Stylesheet {
        val parser = Parser(pos = 0, input = source)
        return Stylesheet(rules = parser.parseRules())
    }

    data class Parser(var pos: Int, var input: String) {
        /// Parse a list of rule sets, separated by optional whitespace.
        fun parseRules(): Vector<Rule> {
            val rules = Vector<Rule>()
            loop@ while (true) {
                consumeWhitespace()
                if (eof()) {
                    break@loop
                }
                rules.addElement(parseRule())
            }
            return rules
        }

        private fun parseRule(): Rule {
            return Rule(selectors = parseSelectors(), declarations = parseDeclarations())
        }

        private fun parseSelectors(): Vector<Selector> {
            val selectors = Vector<Selector>()
            loop@ while (true) {
                selectors.addElement(Simple(parseSimpleSelector()))
                consumeWhitespace()
                when (val nextChar = nextChar()) {
                    ',' -> {
                        consumeChar()
                        consumeWhitespace()
                    }
                    '{' -> {
                        break@loop
                    }
                    else -> {
                        throw Exception("Unexpected character $nextChar in selector list")
                    }
                }
            }
            selectors.sortedBy { it.specificity().toString() }
            return selectors
        }

        private fun parseSimpleSelector(): SimpleSelector {
            val selector = SimpleSelector(tag_name = null, id = null, _class = Vector())
            loop@ while (!eof()) {
                val nextChar = nextChar()
                when {
                    nextChar == '#' -> {
                        consumeChar()
                        selector.id = parseIdentifier()
                    }
                    nextChar == '.' -> {
                        consumeChar()
                        selector._class.addElement(parseIdentifier())
                    }
                    nextChar == '*' -> {
                        consumeChar()
                    }
                    nextChar.validIdentiferChar() -> {
                        selector.tag_name = parseIdentifier()
                    }
                    else -> break@loop
                }
            }
            return selector
        }

        private fun parseDeclarations(): Vector<Declaration> {
            assertEquals('{', consumeChar())
            val declarations = Vector<Declaration>()
            loop@ while (true) {
                consumeWhitespace()
                if (nextChar() == '}') {
                    consumeChar()
                    break@loop
                }
                declarations.addElement(parseDeclaration())
            }
            return declarations
        }

        private fun parseDeclaration(): Declaration {
            val propertyName = parseIdentifier()
            consumeWhitespace()
            assertEquals(':', consumeChar())
            consumeWhitespace()
            val value = parseValue()
            consumeWhitespace()
            assertEquals(';', consumeChar())
            return Declaration(name = propertyName, value = value)
        }

        private fun parseValue(): Value {
            val nextChar = nextChar()
            return when {
                nextChar.isDigit() -> parseLength()
                nextChar == '#' -> parseColor()
                else -> Keyword(parseIdentifier())
            }
        }

        private fun parseLength(): Value {
            return Length(parseFloat(), parseUnit())
        }

        private fun parseFloat(): Float {
            val s = consumeWhile(Char::validDigitOrPoint)
            return s.toFloat()
        }

        private fun parseUnit(): Unit {
            return when (parseIdentifier().toLowerCase()) {
                "px" -> Px()
                else -> throw Exception()
            }
        }

        private fun parseColor(): Value {
            assertEquals('#', consumeChar())
            return ColorValue(
                Color(
                    r = parseHexPair(),
                    g = parseHexPair(),
                    b = parseHexPair(),
                    a = 255.toLong()
                )
            )
        }

        private fun parseHexPair(): Long {
            val s = input.subSequence(pos, pos + 2)
            pos += 2
            return s.toString().toLong(16)
        }

        data class DestructivePosIter(val pos: Int, val iter: Char)

        // Parse a text node.
        private fun parseIdentifier(): String {
            return consumeWhile(Char::validIdentiferChar)
        }

        // Consume and discard zero or more whitespace characters.
        private fun consumeWhitespace() {
            consumeWhile(Char::isWhitespace)
        }

        /// Consume characters until `test` returns false.
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

        // Read the current character without consuming it.
        private fun nextChar(): Char {
            return input[pos]
        }

        // Return true if all input is consumed.
        private fun eof(): Boolean {
            return pos >= input.length
        }
    }
}

fun Char.validIdentiferChar(): Boolean = this.isLetterOrDigit() || this == '-' || this == '_'

fun Char.validDigitOrPoint(): Boolean = this.isDigit() || this == '.'
