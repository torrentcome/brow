package com.torrentcome.brow.test

import com.torrentcome.brow.*
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO
import kotlin.test.assertEquals

class HelloKotlin {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val rootDom = dom()
            val parseHtml = html()
            assertEquals(rootDom, parseHtml)

            val stylesheet = cssMan()
            val parseCss = css()
            assertEquals(stylesheet, parseCss)

            val styleRoot = style2()

            // Since we don't have an actual window, hard-code the "viewport" size.
            val viewport = Dimensions()
            viewport.content.width = 800.0f
            viewport.content.height = 600.0f

            val layoutRoot = Layout.layoutTree(styleRoot, viewport)
            println("* layoutRoot = ")
            println("$layoutRoot")

            // Since we don't have an actual window, hard-code the "viewport" size.
            val initialContainingBlock = Dimensions(Rect(x = 0.0f, y = 0.0f, width = 800.0f, height = 600.0f))
            val canvas = Painting.paint(layoutRoot, initialContainingBlock.content)

            try {
                // retrieve image
                val bi = BufferedImage(canvas.width.toInt(), canvas.height.toInt(), BufferedImage.TYPE_INT_RGB)
                val g: Graphics = bi.graphics

                var row = 0
                var line = 0
                canvas.pixels.forEachIndexed { _, color ->
                    g.color = color.toOfficial()
                    g.drawRect(row, line, 1, 1)
                    if (row >= canvas.width) {
                        line++
                        row = 0
                    }
                    row++
                }

                val outputfile = File("saved.png")
                ImageIO.write(bi, "png", outputfile)
            } catch (e: IOException) {
                println(e)
            }
        }

        @Suppress("unused")
        private fun style(): StyledNode {
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
            return styleTree
        }

        private fun style2(): StyledNode {
            val html2 = "<html> <div class=\"a\"> <div class=\"b\"> <div class=\"c\"> <div class=\"d\"> <div class=\"e\"> <div class=\"f\"> <div class=\"g\"> </div> </div> </div> </div> </div> </div> </div> </html>"
            val css2 = "* { display: block; padding: 12px; } .a { background: #ff0000; } .b { background: #ffa500; } .c { background: #ffff00; } .d { background: #008000; } .e { background: #0000ff; } .f { background: #4b0082; } .g { background: #800080; }"

            val parseHtml2 = Html.parse(html2)
            println("* parseHtml2 = ")
            println("$parseHtml2")
            val parseCss2 = Css.parse(css2)
            println("* parseCss2 = ")
            println("$parseCss2")

            val styleTree: StyledNode = Style.styleTree(root = parseHtml2, stylesheet = parseCss2)
            println("* styleTree = ")
            println("$styleTree")
            return styleTree
        }

        private fun css(): Stylesheet {
            val css = "h1, h2, h3 { margin: auto; color: #cc0000; }\n" +
                    "div.note { margin-bottom: 20px; padding: 10px; }\n" +
                    "#answer { display: none; }"
            val parseCss = Css.parse(css)
            println("* parseCss = ")
            println("$parseCss")
            return parseCss
        }

        private fun cssMan(): Stylesheet {
            val rule1 = Rule(buildSelectors1(), buildDeclarations1())
            val rule2 = Rule(buildSelectors2(), buildDeclarations2())
            val rule3 = Rule(buildSelectors3(), buildDeclarations3())
            val rules = ArrayList<Rule>()
            rules.add(rule1)
            rules.add(rule2)
            rules.add(rule3)
            val stylesheet = Stylesheet(rules)
            println("* stylesheet = ")
            println("$stylesheet")
            return stylesheet
        }

        private fun html(): Node {
            val html = "<html><body>Hello, world!</body></html>"
            val parseHtml = Html.parse(html)
            println("* parseHtml = ")
            println("$parseHtml")
            return parseHtml
        }

        private fun dom(): Node {
            val rootDom = Node(
                    ArrayList(),
                    NodeType.Element(ElementData("html"))
            )
            val bodyDom = Node(
                    ArrayList(),
                    NodeType.Element(ElementData("body"))
            )
            rootDom.children.add(bodyDom)
            bodyDom.children.add(Dom.text("Hello, world!"))
            println("* buildDom =")
            println("$rootDom")
            return rootDom
        }
    }
}

fun Color.toOfficial(): java.awt.Color {
    return java.awt.Color(this.r.toInt(), this.g.toInt(), this.b.toInt())
}