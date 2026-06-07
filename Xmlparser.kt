package org.example

import org.jsoup.Jsoup
import org.jsoup.parser.Parser as JsoupParser

class XmlParser : Parser {
    override fun parse(text: String): Map<String, Any> {
        return parseXml(text)
    }

    fun parseXml(text: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        try {
            val doc = Jsoup.parse(text, "", JsoupParser.xmlParser())
            val root = doc.children().firstOrNull()
            if (root != null) {
                result["rootElement"] = root.tagName()
                result["children"] = root.children().map { element ->
                    mapOf(
                        "tag" to element.tagName(),
                        "text" to element.text(),
                        "attributes" to element.attributes().associate { it.key to it.value }
                    )
                }
                result["text"] = root.text()
            } else {
                result["rawContent"] = text
            }
        } catch (e: Exception) {
            result["error"] = "Failed to parse XML: ${e.message}"
            result["rawContent"] = text
        }
        return result
    }
}