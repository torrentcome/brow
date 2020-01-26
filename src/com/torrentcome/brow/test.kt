package com.torrentcome.brow

import java.util.*
import kotlin.test.assertEquals


class HelloKotlin {
    companion object {
        @JvmStatic fun main(args: Array<String>) {

            println("******** dom *********")
            val root = Node(Vector(), NodeType.Element(ElementData("html")))
            val body = Node(Vector(), NodeType.Element(ElementData("body")))
            root.children.addElement(body)
            body.children.addElement(text("Hello, world!"))
            println("$root")
            println("******** parse *********")
            val s = "<html><body>Hello, world!</body></html>"
            val parse = parse(s)
            println("$parse")
            println("******** assert *********")
            assertEquals(root, parse)
        }
    }
}