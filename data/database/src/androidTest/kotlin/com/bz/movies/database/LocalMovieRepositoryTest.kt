package com.bz.movies.database

import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.database.repository.LocalMovieRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalMovieRepositoryTest {

    private lateinit var database: MoviesDatabase
    private lateinit var repository: LocalMovieRepository

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        repository = LocalMovieRepositoryImpl(
            movieDAO = { database.movieDAO() },
            cachedMovieDAO = { database.cachedMovieDAO() }
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun favoriteMovieCanBeAddedAndRemoved() = runTest {
        assertTrue(repository.insertFavoriteMovie(MOVIE).isSuccess)
        assertEquals(listOf(MOVIE), repository.favoritesMovies.first())

        assertTrue(repository.deleteFavoriteMovie(MOVIE).isSuccess)
        assertEquals(emptyList<MovieDto>(), repository.favoritesMovies.first())
    }

    @Test
    fun playingNowAndPopularCachesAreIndependent() = runTest {
        repository.insertPlayingNowMovies(listOf(MOVIE))
        repository.insertPopularMovies(listOf(OTHER_MOVIE))

        assertEquals(listOf(MOVIE), repository.playingNowMovies.first())
        assertEquals(listOf(OTHER_MOVIE), repository.popularMovies.first())

        repository.clearPlayingNowMovies()

        assertEquals(emptyList<MovieDto>(), repository.playingNowMovies.first())
        assertEquals(listOf(OTHER_MOVIE), repository.popularMovies.first())
        assertEquals(emptyList<MovieDto>(), repository.favoritesMovies.first())
    }

    private companion object {
        val MOVIE = MovieDto(
            id = 1,
            posterUrl = "/poster.jpg",
            title = "Movie",
            publicationDate = "2024-01-01",
            language = "en",
            rating = 70
        )
        val OTHER_MOVIE = MOVIE.copy(id = 2, title = "Other movie")
    }
}
