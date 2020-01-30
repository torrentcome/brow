package com.torrentcome.brow.test

import com.torrentcome.brow.*
import java.util.*

/*
* "h1, h2, h3 { margin: auto; color: #cc0000; }\n" +
* "div.note { margin-bottom: 20px; padding: 10px; }\n" +
* "#answer { display: none; }"
* Rule(selectors=[Simple(simpleSelector=SimpleSelector(tag_name=div, id=null, _class=[note]))], declarations=[Declaration(name=margin-bottom, value=Length(f32=20.0, unit=com.torrentcome.brow.Px@7b23ec81)), Declaration(name=padding, value=Length(f32=10.0, unit=com.torrentcome.brow.Px@6acbcfc0))]),
* Rule(selectors=[Simple(simpleSelector=SimpleSelector(tag_name=null, id=answer, _class=[]))], declarations=[Declaration(name=display, value=Keyword(string=none))])])
* */

internal fun buildSelectors1(): Vector<Selector> {
    val selectorH1 = SimpleSelector(tag_name = "h1", id = null, _class = Vector())
    val selectorH2 = SimpleSelector(tag_name = "h2", id = null, _class = Vector())
    val selectorH3 = SimpleSelector(tag_name = "h3", id = null, _class = Vector())

    val selectors = Vector<Selector>()
    selectors.addElement(Simple(selectorH1))
    selectors.addElement(Simple(selectorH2))
    selectors.addElement(Simple(selectorH3))
    return selectors
}

internal fun buildDeclarations1(): Vector<Declaration> {
    val declaration1 = Declaration(name = "margin", value = Keyword("auto"))
    val declaration2 = Declaration(
            name = "color",
            value = ColorValue(
                    color =
                    Color(
                            r = "cc".toLong(16),
                            g = "00".toLong(16),
                            b = "00".toLong(16),
                            a = 255
                    )
            )
    )

    val declarations = Vector<Declaration>()
    declarations.addElement(declaration1)
    declarations.addElement(declaration2)
    return declarations
}

internal fun buildSelectors2(): Vector<Selector> {

    val classNote = Vector<String>()
    classNote.addElement("note")
    val selectorDivNote = SimpleSelector(tag_name = "div", id = null, _class = classNote)

    val selectors = Vector<Selector>()
    selectors.addElement(Simple(selectorDivNote))

    return selectors
}

internal fun buildDeclarations2(): Vector<Declaration> {
    val declarationMarginBottom = Declaration(name = "margin-bottom", value = Length(20f, Px()))
    val declarationPadding = Declaration(name = "padding", value = Length(10f, Px()))

    val declarations = Vector<Declaration>()
    declarations.addElement(declarationMarginBottom)
    declarations.addElement(declarationPadding)
    return declarations
}

internal fun buildSelectors3(): Vector<Selector> {
    val selectorAnswer = SimpleSelector(tag_name = null, id = "answer", _class = Vector())

    val selectors = Vector<Selector>()
    selectors.addElement(Simple(selectorAnswer))

    return selectors
}


internal fun buildDeclarations3(): Vector<Declaration> {
    val declarationDisplay = Declaration(name = "display", value = Keyword("none"))

    val declarations = Vector<Declaration>()
    declarations.addElement(declarationDisplay)
    return declarations
}
