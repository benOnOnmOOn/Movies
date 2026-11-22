package com.bz.movies.database

import com.bz.movies.database.dao.CachedMovieDAO
import com.bz.movies.database.entity.CachedMovieEntity
import com.bz.movies.database.entity.MovieCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CachedMovieDAOTest {

    private lateinit var database: MoviesDatabase
    private lateinit var dao: CachedMovieDAO

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.cachedMovieDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeMoviesReturnsOnlyRequestedCategory() = runTest {
        dao.insert(listOf(PLAYING_NOW, POPULAR))

        assertEquals(listOf(PLAYING_NOW), dao.observeMovies(MovieCategory.PLAYING_NOW).first())
        assertEquals(listOf(POPULAR), dao.observeMovies(MovieCategory.POPULAR).first())
    }

    @Test
    fun sameMovieCanBeCachedInBothCategories() = runTest {
        val popularCopy = PLAYING_NOW.copy(category = MovieCategory.POPULAR)

        dao.insert(listOf(PLAYING_NOW, popularCopy))

        assertEquals(listOf(PLAYING_NOW), dao.observeMovies(MovieCategory.PLAYING_NOW).first())
        assertEquals(listOf(popularCopy), dao.observeMovies(MovieCategory.POPULAR).first())
    }

    @Test
    fun clearRemovesOnlyRequestedCategory() = runTest {
        dao.insert(listOf(PLAYING_NOW, POPULAR))

        dao.clear(MovieCategory.PLAYING_NOW)

        assertEquals(
            emptyList<CachedMovieEntity>(),
            dao.observeMovies(MovieCategory.PLAYING_NOW).first()
        )
        assertEquals(listOf(POPULAR), dao.observeMovies(MovieCategory.POPULAR).first())
    }

    private companion object {
        val PLAYING_NOW = CachedMovieEntity(
            category = MovieCategory.PLAYING_NOW,
            id = 1,
            posterUrl = "/playing.jpg",
            title = "Playing",
            publicationDate = "2024-01-01",
            language = "en",
            rating = 70
        )
        val POPULAR = PLAYING_NOW.copy(category = MovieCategory.POPULAR, id = 2, title = "Popular")
    }
}
