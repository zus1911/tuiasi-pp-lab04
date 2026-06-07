package ro.tuiasi.pp.lab04

import ro.tuiasi.pp.lab04.notes.FileNoteRepository
import ro.tuiasi.pp.lab04.notes.Note
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.test.*

class NoteRepositoryTest {

    private lateinit var dirTemp: java.nio.file.Path
    private lateinit var repo: FileNoteRepository

    @BeforeTest
    fun initializeaza() {
        // Creăm un director temporar pentru fiecare test
        dirTemp = Files.createTempDirectory("note_test_")
        repo = FileNoteRepository(dirTemp)
    }

    @AfterTest
    fun curata() {
        // Ștergem directorul temporar după fiecare test
        dirTemp.toFile().deleteRecursively()
    }

    private fun creeazaNotiتaTest(
        id: String = "test-id-123",
        author: String = "Ion Ionescu",
        content: String = "Conținut test"
    ) = Note(
        id = id,
        author = author,
        createdAt = LocalDateTime.of(2024, 4, 15, 10, 30, 0),
        content = content
    )

    @Test
    fun `save creeaza fisierul pe disk`() {
        val notita = creeazaNotiتaTest()
        repo.save(notita)

        val fisier = dirTemp.resolve("${notita.id}.txt").toFile()
        assertTrue(fisier.exists(), "Fișierul ${notita.id}.txt ar trebui să existe pe disk")
    }

    @Test
    fun `findById returneaza notita salvata`() {
        val notita = creeazaNotiتaTest()
        repo.save(notita)

        val gasita = repo.findById(notita.id)

        assertNotNull(gasita, "findById ar trebui să returneze notița salvată")
        assertEquals(notita.id, gasita.id)
        assertEquals(notita.author, gasita.author)
        assertEquals(notita.content, gasita.content)
    }

    @Test
    fun `findById returneaza null pentru id inexistent`() {
        val rezultat = repo.findById("id-inexistent")
        assertNull(rezultat, "findById ar trebui să returneze null pentru un ID inexistent")
    }

    @Test
    fun `findById pastreaza data corecta`() {
        val notita = creeazaNotiتaTest()
        repo.save(notita)

        val gasita = repo.findById(notita.id)!!
        assertEquals(notita.createdAt, gasita.createdAt)
    }

    @Test
    fun `findAll returneaza toate notitele salvate`() {
        val notita1 = creeazaNotiتaTest(id = "id-1", content = "Prima notiță")
        val notita2 = creeazaNotiتaTest(id = "id-2", content = "A doua notiță")
        repo.save(notita1)
        repo.save(notita2)

        val toate = repo.findAll()

        assertEquals(2, toate.size)
    }

    @Test
    fun `findAll returneaza lista goala pentru director gol`() {
        val toate = repo.findAll()
        assertTrue(toate.isEmpty())
    }

    @Test
    fun `delete sterge fisierul de pe disk`() {
        val notita = creeazaNotiتaTest()
        repo.save(notita)
        assertTrue(dirTemp.resolve("${notita.id}.txt").toFile().exists())

        repo.delete(notita.id)

        assertFalse(dirTemp.resolve("${notita.id}.txt").toFile().exists(),
            "Fișierul ar trebui șters după apelul delete()")
    }

    @Test
    fun `delete face findById sa returneze null`() {
        val notita = creeazaNotiتaTest()
        repo.save(notita)

        repo.delete(notita.id)

        assertNull(repo.findById(notita.id))
    }

    @Test
    fun `save suprascrie notita existenta`() {
        val original = creeazaNotiتaTest(content = "Conținut original")
        repo.save(original)

        val actualizata = original.copy(content = "Conținut actualizat")
        repo.save(actualizata)

        val gasita = repo.findById(original.id)!!
        assertEquals("Conținut actualizat", gasita.content)
    }

    @Test
    fun `save gestioneaza continut multiliniar`() {
        val continutMultiliniar = "Linia 1\nLinia 2\nLinia 3"
        val notita = creeazaNotiتaTest(content = continutMultiliniar)
        repo.save(notita)

        val gasita = repo.findById(notita.id)!!
        assertEquals(continutMultiliniar, gasita.content)
    }
}
