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
            assertEquals(stylesheet, parseCss)

            val html2 = "<html> <head> <title>Test</title> </head> <div class=\"outer\"> <p class=\"inner\"> Hello, <span id=\"name\">world!</span> </p> <p class=\"inner\" id=\"bye\"> Goodbye! </p> </div> </html>"
            val css2 = "* { display: block; } span { display: inline; } html { width: 600px; padding: 10px; border-width: 1px; margin: auto; background: #ffffff; } head { display: none; } .outer { background: #00ccff; border-color: #666666; border-width: 2px; margin: 50px; padding: 50px; } .inner { border-color: #cc0000; border-width: 4px; height: 100px; margin-bottom: 20px; width: 500px; } .inner#bye { background: #ffff00; } span#name { background: red; color: white; }"

            val parseHtml2 = Html.parse(html2)
            println("* parseHtml2 = ")
            println("$parseHtml2")
            val parseCss2 = Css.parse(css2)
            println("* parseCss2 = ")
            println("$parseCss2")

            val styleTree: StyledNode = Style.styleTree(root = parseHtml2, stylesheet = parseCss2)
            println("* styleTree = ")
            println("$styleTree")

        }
    }
}