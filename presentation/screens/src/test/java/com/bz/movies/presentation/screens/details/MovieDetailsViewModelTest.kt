package com.bz.movies.presentation.screens.details

import app.cash.turbine.test
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MovieItem
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.model.MoveDetailDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.random.Random

class MovieDetailsViewModelTest {

    private val movieRepository: MovieRepository = mockk()

    @Test
    fun `when viewmodel is init it should not make any network call `() = runTest {
        coEvery { movieRepository.getMovieDetail(any()) } returns Result.success(
            SUCCESS_MOVIE_DETAIL
        )

        val viewModel = MovieDetailsViewModel(movieRepository)
        viewModel.state.test {
            val actualItem = awaitItem()
            val expectedItem = MovieDetailState()
            assertEquals(expectedItem, actualItem)

            coVerify(exactly = 0) { movieRepository.getPopularMovies(any()) }

            expectNoEvents()
        }
    }

    @Test
    fun `when viewmodel fetchMovieDetails is called should fetch data from network `() = runTest {
        coEvery { movieRepository.getMovieDetail(any()) } returns Result.success(
            SUCCESS_MOVIE_DETAIL
        )
        mockkObject(Random)
        every { Random.nextInt(any(), any()) } returns 69

        val viewModel = MovieDetailsViewModel(movieRepository)
        advanceUntilIdle()
        viewModel.fetchMovieDetails(1234)
        viewModel.state.test {
            val actualItem = awaitItem()
            assertEquals(EXPECTED_DETAILS_STATE, actualItem)
        }
    }

    companion object {

        val EXPECTED_DETAILS_STATE = MovieDetailState(
            isLoading = false, MovieItem(
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

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            Dispatchers.resetMain()
        }
    }
}
