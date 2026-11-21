package com.bz.movies.presentation.screens.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.bz.dto.MoveDetailDto
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieItem
import com.bz.network.repository.HttpException
import com.bz.network.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class MovieDetailsViewModelTest {

    private val movieRepository: MovieRepository = mockk()

    @Test
    fun `when viewmodel is init it should fetch details for the route movie id`() = runTest {
        coEvery { movieRepository.getMovieDetail(any()) } returns Result.success(
            SUCCESS_MOVIE_DETAIL
        )
        mockkObject(Random)
        every { Random.nextInt(any(), any()) } returns 69

        val viewModel =
            MovieDetailsViewModel(SavedStateHandle(mapOf("id" to MOVIE_ID))) { movieRepository }

        assertEquals(EXPECTED_DETAILS_STATE, viewModel.state.first { !it.isLoading })
        coVerify(exactly = 1) { movieRepository.getMovieDetail(MOVIE_ID) }
        unmockkObject(Random)
    }

    @Test
    fun `when we get network error then we viewmodel should emit NetworkConnectionError`() =
        runTest {
            coEvery {
                movieRepository.getMovieDetail(
                    any()
                )
            } returns Result.failure(HttpException(""))

            val viewModel =
                MovieDetailsViewModel(SavedStateHandle(mapOf("id" to MOVIE_ID))) { movieRepository }
            viewModel.effect.test {
                assertEquals(MovieEffect.NetworkConnectionError, awaitItem())
                expectNoEvents()
            }

            coVerify(exactly = 1) { movieRepository.getMovieDetail(any()) }
        }

    @Test
    fun `when we get unhandled error then we viewmodel should emit UnknownError`() = runTest {
        coEvery {
            movieRepository.getMovieDetail(any())
        } returns Result.failure(IllegalStateException())

        val viewModel =
            MovieDetailsViewModel(SavedStateHandle(mapOf("id" to MOVIE_ID))) { movieRepository }
        viewModel.effect.test {
            assertEquals(MovieEffect.UnknownError, awaitItem())
            expectNoEvents()
        }

        coVerify(exactly = 1) { movieRepository.getMovieDetail(any()) }
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
//        MainScope().cancel()
    }

    companion object {
        private const val MOVIE_ID = 1234

        internal val EXPECTED_DETAILS_STATE = MovieDetailState(
            isLoading = false,
            MovieItem(
                id = 1234,
                posterUrl = "poster_url",
                rating = 69,
                language = "polish",
                title = "Wnuki",
                releaseDate = "publicationDate"
            )
        )

        val SUCCESS_MOVIE_DETAIL = MoveDetailDto(
            id = 1234,
            posterUrl = "poster_url",
            publicationDate = "publicationDate",
            language = "polish",
            title = "Wnuki",
            genre = setOf("Genre"),
            overview = "bornig"
        )
    }
}
