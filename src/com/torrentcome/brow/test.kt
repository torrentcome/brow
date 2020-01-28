package com.torrentcome.brow

import java.util.*
import kotlin.test.assertEquals


class HelloKotlin {
    companion object {
        @JvmStatic fun main(args: Array<String>) {

            val rootDom = Node(Vector(), NodeType.Element(ElementData("html")))
            val bodyDom = Node(Vector(), NodeType.Element(ElementData("body")))
            rootDom.children.addElement(bodyDom)
            bodyDom.children.addElement(Dom.text("Hello, world!"))
            println("* dom = $rootDom")

            val html = "<html><body>Hello, world!</body></html>"
            val parseHtml = Html.parse(html)
            println("* parseHtml = $parseHtml")
            assertEquals(rootDom, parseHtml)

            val css = "h1, h2, h3 { margin: auto; color: #cc0000; }\n" +
                    "div.note { margin-bottom: 20px; padding: 10px; }\n" +
                    "#answer { display: none; }"
            val parseCss = Css.parse(css)
            println("* parseCss = $parseCss")

        }
    }
}