package com.torrentcome.brow

import java.util.*

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
    data class Length(var f32: Int, var unit: Unit)
    data class ColorValue(var color: Color)
}

open class Color(
        var r: Byte = -1,
        var g: Byte = -1,
        var b: Byte = -1,
        var a: Byte = -1)

class Copy : Color()

data class Specificity(var a: Int, var b: Int, var c: Int)
