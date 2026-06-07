package ro.tuiasi.pp.lab04

import ro.tuiasi.pp.lab04.cinema.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CinemaServiceTest {

    private fun creeazaService(): Pair<CinemaService, InMemoryCinemaRepository> {
        val repo = InMemoryCinemaRepository()
        val service = CinemaService(repo)
        return Pair(service, repo)
    }

    private val filmTest = Movie(id = 1, title = "Avengers", duration = 180)
    private val scaunDisponibil = Seat(row = 1, number = 5, isAvailable = true)
    private val scaunOcupat = Seat(row = 2, number = 3, isAvailable = false)

    @Test
    fun `bookTicket creeaza bilet corect`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(scaunDisponibil)

        val bilet = service.bookTicket(filmTest, scaunDisponibil, 25.0)

        assertEquals(filmTest, bilet.movie)
        assertEquals(25.0, bilet.price)
    }

    @Test
    fun `bookTicket marcheaza scaunul ca indisponibil`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(scaunDisponibil)

        service.bookTicket(filmTest, scaunDisponibil, 25.0)

        val scaunActualizat = repo.findSeat(scaunDisponibil.row, scaunDisponibil.number)
        assertFalse(scaunActualizat?.isAvailable ?: true,
            "Scaunul ar trebui marcat ca indisponibil după rezervare")
    }

    @Test
    fun `bookTicket arunca exceptie pentru scaun indisponibil`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(scaunOcupat)

        assertFailsWith<IllegalStateException> {
            service.bookTicket(filmTest, scaunOcupat, 25.0)
        }
    }

    @Test
    fun `cancelTicket elibereaza scaunul`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(scaunDisponibil)

        val bilet = service.bookTicket(filmTest, scaunDisponibil, 25.0)
        service.cancelTicket(bilet)

        val scaunActualizat = repo.findSeat(scaunDisponibil.row, scaunDisponibil.number)
        assertTrue(scaunActualizat?.isAvailable ?: false,
            "Scaunul ar trebui eliberat după anularea biletului")
    }

    @Test
    fun `availableSeats returneaza doar scaunele disponibile`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(Seat(1, 1, isAvailable = true))
        repo.addSeat(Seat(1, 2, isAvailable = false))
        repo.addSeat(Seat(1, 3, isAvailable = true))

        val disponibile = service.availableSeats(filmTest)

        assertEquals(2, disponibile.size)
        assertTrue(disponibile.all { it.isAvailable })
    }

    @Test
    fun `availableSeats returneaza lista goala cand toate scaunele sunt ocupate`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(Seat(1, 1, isAvailable = false))
        repo.addSeat(Seat(1, 2, isAvailable = false))

        val disponibile = service.availableSeats(filmTest)

        assertTrue(disponibile.isEmpty())
    }

    @Test
    fun `bookTicket urmat de cancelTicket permite o noua rezervare`() {
        val (service, repo) = creeazaService()
        repo.addMovie(filmTest)
        repo.addSeat(scaunDisponibil)

        val bilet1 = service.bookTicket(filmTest, scaunDisponibil, 25.0)
        service.cancelTicket(bilet1)

        // Scaunul trebuie să fie disponibil din nou
        val scaunCurent = repo.findSeat(scaunDisponibil.row, scaunDisponibil.number)!!
        val bilet2 = service.bookTicket(filmTest, scaunCurent, 30.0)
        assertEquals(filmTest, bilet2.movie)
    }
}
