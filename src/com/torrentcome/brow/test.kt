package com.torrentcome.brow

import java.util.*
import kotlin.test.assertEquals

class HelloKotlin {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val rootDom = Node(Vector(), NodeType.Element(ElementData("html")))
            val bodyDom = Node(Vector(), NodeType.Element(ElementData("body")))
            rootDom.children.addElement(bodyDom)
            bodyDom.children.addElement(Dom.text("Hello, world!"))
            println("* buildDom =")
            println("$rootDom")

            val html = "<html><body>Hello, world!</body></html>"
            val parseHtml = Html.parse(html)
            println("* parseHtml = ")
            println("$parseHtml")
            assertEquals(rootDom, parseHtml)

            val rule = Rule(buildSelectors(), buildDeclarations())
            val rules = Vector<Rule>()
            rules.addElement(rule)
            val stylesheet = Stylesheet(rules)
            println("* stylesheet = ")
            println("$stylesheet")

            val css = "h1, h2, h3 { margin: auto; color: #cc0000; }\n" +
                    "div.note { margin-bottom: 20px; padding: 10px; }\n" +
                    "#answer { display: none; }"
            val parseCss = Css.parse(css)
            println("* parseCss = ")
            println("$parseCss")
        }

        private fun buildDeclarations(): Vector<Declaration> {
            val declaration1 = Declaration(name = "margin", value = Keyword("auto"))
            val declaration2 = Declaration(
                name = "color", value = ColorValue(
                    color =
                    Color(
                        r = "cc".toLong(16),
                        g = "00".toLong(16),
                        b = "00".toLong(16)
                    )
                )
            )

            val declarations = Vector<Declaration>()
            declarations.addElement(declaration1)
            declarations.addElement(declaration2)
            return declarations
        }

        private fun buildSelectors(): Vector<Selector> {
            val selectorH1 = SimpleSelector(tag_name = "h1", id = null, _class = Vector())
            val selectorH2 = SimpleSelector(tag_name = "h2", id = null, _class = Vector())
            val selectorH3 = SimpleSelector(tag_name = "h3", id = null, _class = Vector())

            val selectors = Vector<Selector>()
            selectors.addElement(Simple(selectorH1))
            selectors.addElement(Simple(selectorH2))
            selectors.addElement(Simple(selectorH3))
            return selectors
        }
    }
}