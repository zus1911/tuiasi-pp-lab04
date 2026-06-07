package org.example

import org.yaml.snakeyaml.Yaml

class YamlParser : Parser {
    override fun parse(text: String): Map<String, Any> {
        return parseYaml(text)
    }

    fun parseYaml(text: String): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        try {
            val yaml = Yaml()
            val parsed = yaml.load<Any>(text)
            when (parsed) {
                is Map<*, *> -> parsed.forEach { (key, value) ->
                    if (key != null && value != null) result[key.toString()] = value
                }
                is List<*> -> result["items"] = parsed.filterNotNull()
                else -> result["value"] = parsed ?: "null"
            }
        } catch (e: Exception) {
            result["error"] = "Failed to parse YAML: ${e.message}"
            result["rawContent"] = text
        }
        return result
    }
}