package org.example

fun main() {

    // Exercitiul 1
    println("JSON Example:")
    val jsonCrawler = Crawler("https://jsonplaceholder.typicode.com/todos/1")
    val jsonResult = jsonCrawler.processContent("application/json")
    println("Parsed JSON result:")
    jsonResult.forEach { (k, v) -> println("  $k = $v") }

    println()

    println("XML Example:")
    val xmlCrawler = Crawler("https://www.w3schools.com/xml/note.xml")
    val xmlResult = xmlCrawler.processContent("application/xml")
    println("Parsed XML result:")
    xmlResult.forEach { (k, v) -> println("  $k = $v") }

    println()

    println("YAML Example:")
    val yamlParser = YamlParser()
    val sampleYaml = """
        name: Web Crawler
        version: 1.0
        features:
          - JSON parsing
          - XML parsing
          - YAML parsing
        active: true
    """.trimIndent()
    val yamlResult = yamlParser.parseYaml(sampleYaml)
    println("Parsed YAML result:")
    yamlResult.forEach { (k, v) -> println("  $k = $v") }
}