package com.torrentcome.brow.test

import com.torrentcome.brow.*
import java.util.*
import kotlin.test.assertEquals

class HelloKotlin {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val rootDom = Node(
                Vector(),
                NodeType.Element(ElementData("html"))
            )
            val bodyDom = Node(
                Vector(),
                NodeType.Element(ElementData("body"))
            )
            rootDom.children.addElement(bodyDom)
            bodyDom.children.addElement(Dom.text("Hello, world!"))
            println("* buildDom =")
            println("$rootDom")

            val html = "<html><body>Hello, world!</body></html>"
            val parseHtml = Html.parse(html)
            println("* parseHtml = ")
            println("$parseHtml")
            assertEquals(rootDom, parseHtml)

            val rule1 = Rule(buildSelectors1(), buildDeclarations1())
            val rule2 = Rule(buildSelectors2(), buildDeclarations2())
            val rule3 = Rule(buildSelectors3(), buildDeclarations3())
            val rules = Vector<Rule>()
            rules.addElement(rule1)
            rules.addElement(rule2)
            rules.addElement(rule3)
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


    }
}