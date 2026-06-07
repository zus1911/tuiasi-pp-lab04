package ro.tuiasi.pp.lab04

import ro.tuiasi.pp.lab04.crawler.JsonParser
import ro.tuiasi.pp.lab04.crawler.XmlParser
import ro.tuiasi.pp.lab04.crawler.YamlParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContentParserTest {

    // ----- JsonParser -----

    @Test
    fun `JsonParser parseaza titlu si autor`() {
        val json = """{"titlu": "Articol Kotlin", "autor": "Ion"}"""
        val parser = JsonParser()
        val rezultat = parser.parse(json)
        assertEquals("Articol Kotlin", rezultat["titlu"])
        assertEquals("Ion", rezultat["autor"])
    }

    @Test
    fun `JsonParser parseaza valoare numerica`() {
        val json = """{"an": 2024, "luna": 4}"""
        val parser = JsonParser()
        val rezultat = parser.parse(json)
        assertNotNull(rezultat["an"])
        assertEquals(2024, (rezultat["an"] as? Int) ?: (rezultat["an"].toString().toInt()))
    }

    @Test
    fun `JsonParser returneaza mapa goala pentru JSON gol`() {
        val parser = JsonParser()
        val rezultat = parser.parse("{}")
        assertTrue(rezultat.isEmpty())
    }

    // ----- XmlParser -----

    @Test
    fun `XmlParser parseaza elemente simple`() {
        val xml = """
            <date>
              <titlu>Articol XML</titlu>
              <autor>Maria</autor>
            </date>
        """.trimIndent()
        val parser = XmlParser()
        val rezultat = parser.parse(xml)
        assertEquals("Articol XML", rezultat["titlu"])
        assertEquals("Maria", rezultat["autor"])
    }

    @Test
    fun `XmlParser returneaza mapa cu toate elementele`() {
        val xml = """
            <root>
              <a>valoare1</a>
              <b>valoare2</b>
              <c>valoare3</c>
            </root>
        """.trimIndent()
        val parser = XmlParser()
        val rezultat = parser.parse(xml)
        assertEquals(3, rezultat.size)
        assertEquals("valoare1", rezultat["a"])
        assertEquals("valoare2", rezultat["b"])
        assertEquals("valoare3", rezultat["c"])
    }

    // ----- YamlParser -----

    @Test
    fun `YamlParser parseaza perechi cheie valoare simple`() {
        val yaml = """
            titlu: Articol YAML
            autor: Gheorghe
        """.trimIndent()
        val parser = YamlParser()
        val rezultat = parser.parse(yaml)
        assertEquals("Articol YAML", rezultat["titlu"])
        assertEquals("Gheorghe", rezultat["autor"])
    }

    @Test
    fun `YamlParser parseaza valoare numerica`() {
        val yaml = "an: 2024"
        val parser = YamlParser()
        val rezultat = parser.parse(yaml)
        assertNotNull(rezultat["an"])
        assertEquals(2024, (rezultat["an"] as? Int) ?: (rezultat["an"].toString().toInt()))
    }

    @Test
    fun `YamlParser ignora linii goale`() {
        val yaml = """
            cheie1: val1

            cheie2: val2
        """.trimIndent()
        val parser = YamlParser()
        val rezultat = parser.parse(yaml)
        assertEquals(2, rezultat.size)
    }
}
