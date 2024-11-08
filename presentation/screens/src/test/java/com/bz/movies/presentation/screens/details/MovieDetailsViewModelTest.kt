package com.bz.movies.presentation.screens.details

import app.cash.turbine.test
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieItem
import com.bz.network.repository.HttpException
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.model.MoveDetailDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import timber.log.Timber

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
        viewModel.fetchMovieDetails(1234)
        viewModel.state.test {
            awaitItem()
            val actualItem = awaitItem()
            assertEquals(EXPECTED_DETAILS_STATE, actualItem)
        }
    }

    @Test
    fun `when we get network error then we viewmodel should emit NetworkConnectionError`() =
        runTest {
            coEvery {
                movieRepository.getMovieDetail(
                    any()
                )
            } returns Result.failure(HttpException(""))

            val timberPlantTree: Timber.Tree = mockk(relaxed = true)
            Timber.plant(timberPlantTree)
            verify(exactly = 0) { timberPlantTree.e(any<Throwable>()) }

            val viewModel = MovieDetailsViewModel(movieRepository)
            viewModel.fetchMovieDetails(1234)
            viewModel.effect.test {
                assertEquals(MovieEffect.NetworkConnectionError, awaitItem())
                expectNoEvents()
            }

            coVerify(exactly = 1) { movieRepository.getMovieDetail(any()) }
            verify(exactly = 1) { timberPlantTree.e(any<Throwable>()) }

            Timber.uproot(timberPlantTree)
        }

    @Test
    fun `when we get unhandled error then we viewmodel should emit UnknownError`() = runTest {
        coEvery {
            movieRepository.getMovieDetail(any())
        } returns Result.failure(IllegalStateException())

        val timberPlantTree: Timber.Tree = mockk(relaxed = true)
        Timber.plant(timberPlantTree)
        verify(exactly = 0) { timberPlantTree.e(any<Throwable>()) }

        val viewModel = MovieDetailsViewModel(movieRepository)
        viewModel.fetchMovieDetails(1234)
        viewModel.effect.test {
            assertEquals(MovieEffect.UnknownError, awaitItem())
            expectNoEvents()
        }

        coVerify(exactly = 1) { movieRepository.getMovieDetail(any()) }
        verify(exactly = 1) { timberPlantTree.e(any<Throwable>()) }

        Timber.uproot(timberPlantTree)
    }

    companion object {

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

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Dispatchers.setMain(StandardTestDispatcher())
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            Dispatchers.resetMain()
            unmockkAll()
        }
    }
}
