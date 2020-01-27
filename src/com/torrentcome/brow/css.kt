package com.torrentcome.brow

import java.util.*
import kotlin.reflect.KFunction1

data class Stylesheet(var rules: Vector<Rule>)

data class Rule(var selectors: Vector<Selector>, var declarations: Vector<Declaration>)

abstract class Selector {
    data class Simple(var simpleSelector: SimpleSelector) {
        fun specificity(): Specificity {
            // http://www.w3.org/TR/selectors/#specificity
            val a: Int = simpleSelector.id?.length ?: 0
            val b = simpleSelector._class.size
            val c = simpleSelector.tag_name?.length ?: 0
            return Specificity(a, b, c)
        }
    }
}

data class SimpleSelector(var tag_name: String?, var id: String?, var _class: Vector<String>)

data class Declaration(var name: String, var value: Value)

abstract class Value {
    data class Keyword(var string: String)
    data class Length(var f32: Float, var unit: Unit)
    data class ColorValue(var color: Color)

    /// Return the size of a length in px, or zero for non-lengths.
    fun to_px(value: Any?): Float {
        return when (value) {
            is Value.Length -> value.f32
            else -> 0.0f
        }
    }
}

open class Color(
        var r: Byte = -1,
        var g: Byte = -1,
        var b: Byte = -1,
        var a: Byte = -1)

class Copy : Color()

data class Specificity(var a: Int, var b: Int, var c: Int)

/// Parse a whole CSS stylesheet.
object Css {

    fun parse(source: String): Stylesheet {
        val parser = Parser (pos= 0, input= source )
        return Stylesheet (rules= parser.parse_rules())
    }

    data class Parser(var pos: Int, var input: String) {
        /// Parse a list of rule sets, separated by optional whitespace.
        /*fun parse_rules() : Vector<Rule> {
            val rules = Vector<Rule>()
            while(true) {
                consume_whitespace()
                if (eof()) {
                    break
                }
                rules.addElement(parse_rule())
            }
            return rules
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
*/

        fun parse_rules(): Vector<Rule> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        // Return the current character, and advance self.pos to the next character.
        private fun consumeChar(): Char {
            val iter: Char = input.getOrElse(pos) { ' ' }
            val (p, cur_char) = DestructivePosIter(1, iter)
            pos += p
            return cur_char
        }

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

        data class DestructivePosIter(val pos: Int, val iter: Char)

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

fun Char.validIdentiferChar(): Boolean = this.isLetterOrDigit() || this == '-'|| this == '_'
