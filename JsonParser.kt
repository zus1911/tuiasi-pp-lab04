package org.example

import org.json.JSONObject

class JsonParser : Parser {
    override fun parse(text: String): Map<String, Any> {
        return parseJson(text)
    }

    fun parseJson(text: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        try {
            val jsonObject = JSONObject(text)
            for (key in jsonObject.keys()) {
                result[key] = jsonObject.get(key)
            }
        } catch (e: Exception) {
            result["error"] = "Failed to parse JSON: ${e.message}"
            result["rawContent"] = text
        }
        return result
    }
}