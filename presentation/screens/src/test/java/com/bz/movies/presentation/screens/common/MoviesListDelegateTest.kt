package com.bz.movies.presentation.screens.common

import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toDTO
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.network.repository.NoInternetException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MoviesListDelegateTest {

    private val localMovieRepository: LocalMovieRepository = mockk(relaxed = true)

    private val fetchCalls = MutableStateFlow(0)
    private val clearCalls = MutableStateFlow(0)
    private val markRefreshedCalls = MutableStateFlow(0)
    private val cachedLists = MutableStateFlow<List<List<MovieDto>>>(emptyList())

    @Test
    fun `on start it fetches movies, caches them and shows the cached list`() = runTest {
        val delegate = createDelegate()

        assertEquals(
            MoviesState(
                isLoading = false,
                isRefreshing = false,
                playingNowMovies = listOf(MOVIE.toMovieItem())
            ),
            delegate.state.first { !it.isLoading }
        )
        cachedLists.first { it.isNotEmpty() }

        assertEquals(listOf(listOf(MOVIE)), cachedLists.value)
        assertEquals(1, fetchCalls.value)
        assertEquals(1, markRefreshedCalls.value)
        assertEquals(0, clearCalls.value)
    }

    @Test
    fun `when there is no internet it emits NetworkConnectionError and caches nothing`() = runTest {
        val delegate = createDelegate(
            cachedMovies = flowOf(emptyList()),
            fetchResult = Result.failure(NoInternetException())
        )

        assertEquals(MovieEffect.NetworkConnectionError, delegate.effect.first())
        assertEquals(
            MoviesState(isLoading = false, isRefreshing = false),
            delegate.state.first { !it.isLoading }
        )
        assertEquals(emptyList<List<MovieDto>>(), cachedLists.value)
        assertEquals(0, markRefreshedCalls.value)
    }

    @Test
    fun `when fetching fails with an unexpected error it emits UnknownError`() = runTest {
        val delegate = createDelegate(fetchResult = Result.failure(IllegalStateException()))

        assertEquals(MovieEffect.UnknownError, delegate.effect.first())
    }

    @Test
    fun `when reading the cache fails it emits UnknownError and stops loading`() = runTest {
        val delegate = createDelegate(cachedMovies = flow { throw IllegalStateException() })

        assertEquals(MovieEffect.UnknownError, delegate.effect.first())
        assertEquals(
            MoviesState(isLoading = false, isRefreshing = false),
            delegate.state.first { !it.isLoading }
        )
    }

    @Test
    fun `refresh event clears the cache and fetches again`() = runTest {
        val delegate = createDelegate()
        cachedLists.first { it.size == 1 }

        delegate.sendEvent(MovieEvent.Refresh)

        cachedLists.first { it.size == 2 }
        assertEquals(2, fetchCalls.value)
        assertEquals(1, clearCalls.value)
        assertEquals(2, markRefreshedCalls.value)
    }

    @Test
    fun `movie click event saves the movie to favorites`() = runTest {
        coEvery { localMovieRepository.insertFavoriteMovie(any()) } returns Result.success(Unit)
        val delegate = createDelegate()
        val item = MOVIE.toMovieItem()

        delegate.sendEvent(MovieEvent.OnMovieClicked(item)).join()

        coVerify(exactly = 1) { localMovieRepository.insertFavoriteMovie(item.toDTO()) }
    }

    private fun TestScope.createDelegate(
        cachedMovies: Flow<List<MovieDto>> = flowOf(listOf(MOVIE)),
        fetchResult: Result<List<MovieDto>> = Result.success(listOf(MOVIE))
    ) = MoviesListDelegate(
        scope = backgroundScope,
        ioDispatcher = StandardTestDispatcher(testScheduler),
        localMovieRepository = { localMovieRepository },
        cachedMovies = { cachedMovies },
        fetchMovies = {
            fetchCalls.update { it + 1 }
            fetchResult
        },
        cacheMovies = { movies ->
            cachedLists.update { it + listOf(movies) }
            Result.success(Unit)
        },
        clearCache = {
            clearCalls.update { it + 1 }
            Result.success(Unit)
        },
        markRefreshed = {
            markRefreshedCalls.update { it + 1 }
            Result.success(Unit)
        },
        lastRefreshDate = { Result.success(Instant.fromEpochSeconds(0)) }
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private companion object {
        val MOVIE = MovieDto(
            id = 1,
            posterUrl = "poster.jpg",
            title = "Title",
            publicationDate = "2024-01-01",
            language = "en",
            rating = 80
        )
    }
}
