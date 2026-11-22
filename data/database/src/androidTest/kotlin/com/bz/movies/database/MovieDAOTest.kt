package com.bz.movies.database

import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.entity.MovieEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieDAOTest {

    private lateinit var database: MoviesDatabase
    private lateinit var dao: MovieDAO

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.movieDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertedMovieIsObserved() = runTest {
        dao.insert(FIRST)

        assertEquals(listOf(FIRST), dao.observeAllMovies().first())
    }

    @Test
    fun insertWithSameIdReplacesMovie() = runTest {
        dao.insert(FIRST)
        dao.insert(FIRST.copy(title = "Updated"))

        assertEquals(listOf(FIRST.copy(title = "Updated")), dao.observeAllMovies().first())
    }

    @Test
    fun deletedMovieIsNoLongerObserved() = runTest {
        dao.insert(listOf(FIRST, SECOND))

        dao.delete(FIRST)

        assertEquals(listOf(SECOND), dao.observeAllMovies().first())
    }

    private companion object {
        val FIRST = MovieEntity(
            id = 1,
            posterUrl = "/first.jpg",
            title = "First",
            publicationDate = "2024-01-01",
            language = "en",
            rating = 70
        )
        val SECOND = FIRST.copy(id = 2, title = "Second")
    }
}
