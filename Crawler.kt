package org.example

import java.net.URI
import java.net.HttpURLConnection

data class HttpResponse(
    val statusCode: Int,
    val text: String,
    val headers: Map<String, String>
)

class Crawler(private val url: String) {

    fun getResource(): HttpResponse {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")

        val statusCode = connection.responseCode
        val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
        val text = stream.bufferedReader().readText()
        val headers = connection.headerFields
            .filterKeys { it != null }
            .mapKeys { it.key }
            .mapValues { it.value.firstOrNull() ?: "" }

        connection.disconnect()
        return HttpResponse(statusCode, text, headers)
    }

    fun processContent(contentType: String): Map<String, Any> {
        val response = getResource()

        val parser: Parser = when {
            contentType.contains("json", ignoreCase = true) -> JsonParser()
            contentType.contains("xml", ignoreCase = true)  -> XmlParser()
            contentType.contains("yaml", ignoreCase = true) ||
                    contentType.contains("yml", ignoreCase = true)  -> YamlParser()
            else -> {
                val responseContentType = response.headers["Content-Type"] ?: ""
                when {
                    responseContentType.contains("json", ignoreCase = true) -> JsonParser()
                    responseContentType.contains("xml",  ignoreCase = true) -> XmlParser()
                    responseContentType.contains("yaml", ignoreCase = true) -> YamlParser()
                    else -> JsonParser()
                }
            }
        }

        println("Fetching:     $url")
        println("Status:       ${response.statusCode}")
        println("Content-Type: ${response.headers["Content-Type"]}")
        println("Parser:       ${parser::class.simpleName}")
        println("---")

        return parser.parse(response.text)
    }
}