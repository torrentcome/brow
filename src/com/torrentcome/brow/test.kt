package com.torrentcome.brow

class HelloKotlin {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            println("******** dom *********")

            val text = text("hello")
            println("text =$text")
            val hashMap = HashMap<String, String>()
            hashMap["id"] = "myId"
            val elementData = ElementData(tag_name = "tag_name", attributes = hashMap)
            println("text =${elementData.classes()}")
            hashMap["class"] = "something and other"
            println("text =${elementData.classes()}")
            println("text =${elementData.id()}")

            println("******** html *********")

        }
    }
}