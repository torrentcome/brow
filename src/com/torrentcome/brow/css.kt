package com.torrentcome.brow

import java.lang.reflect.GenericDeclaration
import java.util.*

data class Stylesheet (var rules : Vector<Rule>)

data class Rule (var selectors : Vector<Selector>, var declarations : Vector<Declaration>)

abstract class Selector {
    data class Simple(var simpleSelector : SimpleSelector)
}

data class SimpleSelector(var tag_name : String?, var id: String?, var _class: Vector<String>)

data class Declaration(var name: String, var value: Value)

abstract class Value {
    data class Keyword(var string : String)
    data class Length(var f32 : Int, var unit : Unit)
    data class ColorValue(var color : Color)
}

data class Color (
        var r : Short,
        var g : Short,
        var b : Short,
        var a : Short
)

