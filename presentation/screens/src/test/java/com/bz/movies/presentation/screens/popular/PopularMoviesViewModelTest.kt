package com.bz.movies.presentation.screens.popular

import app.cash.turbine.test
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MovieItem
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.NoInternetException
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PopularMoviesViewModelTest {

    private val movieRepository: MovieRepository = mockk()
    private val localMovieRepository: LocalMovieRepository = mockk(relaxed = true)

    private val storeRepository: DataStoreRepository = mockk(relaxed = true) {
        coEvery { getPopularRefreshDate() } returns Result.success(Instant.now())
    }

    @Test
    fun `when local storage is empty then it  should load data from network and store it `() =
        runTest {
            every { localMovieRepository.popularMovies } returns flowOf(emptyList())
            coEvery { movieRepository.getPopularMovies(any()) } returns Result.success(emptyList())

            val viewModel = PopularMoviesViewModel(
                movieRepository = Lazy { movieRepository },
                localMovieRepository = Lazy { localMovieRepository },
                dataStoreRepository = Lazy { storeRepository }
            )
            viewModel.state.test {
                val actualItem = awaitItem()
                val expectedItem = MoviesState()
                assertEquals(expectedItem, actualItem)

                val actualItem2 = awaitItem()
                val expectedItem2 = MoviesState(isLoading = false, isRefreshing = false)
                assertEquals(expectedItem2, actualItem2)

                expectNoEvents()
            }

            verify(exactly = 1) { localMovieRepository.popularMovies }
            coVerify(exactly = 1) { localMovieRepository.insertPopularMovies(any()) }
            coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }
        }

    @Test
    fun `when there is problem with load data from locale storage we should display error`() =
        runTest {
            every { localMovieRepository.popularMovies } returns flow { throw IllegalStateException() }
            coEvery { movieRepository.getPopularMovies(any()) } returns Result.success(emptyList())

            val viewModel = PopularMoviesViewModel(
                movieRepository = Lazy { movieRepository },
                localMovieRepository = Lazy { localMovieRepository },
                dataStoreRepository = Lazy { storeRepository }
            )

            viewModel.effect.test {
                awaitItem()

                expectNoEvents()
            }

            verify(exactly = 1) { localMovieRepository.popularMovies }
            coVerify(exactly = 1) { localMovieRepository.insertPopularMovies(any()) }
            coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }
        }

    @Test
    fun `when we have network issue while fetching data then viewmodel should emit NetworkConnectionError effect`() =
        runTest {
            every { localMovieRepository.popularMovies } returns flowOf(emptyList())
            coEvery {
                movieRepository.getPopularMovies(any())
            } returns Result.failure(NoInternetException())

            val viewModel = PopularMoviesViewModel(
                movieRepository = Lazy { movieRepository },
                localMovieRepository = Lazy { localMovieRepository },
                dataStoreRepository = Lazy { storeRepository }
            )

            viewModel.effect.test {
                val actualEffect = awaitItem()
                val expectedEffect = MovieEffect.NetworkConnectionError
                assertEquals(expectedEffect, actualEffect)
            }
        }

    @Test
    fun `when we have network issue while fetching data then localMovieRepository should not save data`() =
        runTest {
            every { localMovieRepository.popularMovies } returns flowOf(emptyList())
            coEvery {
                movieRepository.getPopularMovies(any())
            } returns Result.failure(NoInternetException())

            val viewModel = PopularMoviesViewModel(
                movieRepository = Lazy { movieRepository },
                localMovieRepository = Lazy { localMovieRepository },
                dataStoreRepository = Lazy { storeRepository }
            )

            viewModel.state.test {
                val actualItem = awaitItem()
                val expectedItem = MoviesState(isLoading = true, isRefreshing = false)
                assertEquals(expectedItem, actualItem)

                val actualItem2 = awaitItem()
                val expectedItem2 = MoviesState(isLoading = false, isRefreshing = false)
                assertEquals(expectedItem2, actualItem2)
                expectNoEvents()
            }

            verify(exactly = 1) { localMovieRepository.popularMovies }
            coVerify(exactly = 0) { localMovieRepository.insertPopularMovies(any()) }
            coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }
        }

    @Test
    fun `when we get non internet issue exception while fetching data then viewmodel should emit UnknownError effect`() =
        runTest {
            every { localMovieRepository.popularMovies } returns flowOf(emptyList())
            coEvery {
                movieRepository.getPopularMovies(any())
            } returns Result.failure(IllegalStateException())

            val viewModel = PopularMoviesViewModel(
                movieRepository = Lazy { movieRepository },
                localMovieRepository = Lazy { localMovieRepository },
                dataStoreRepository = Lazy { storeRepository }
            )

            viewModel.effect.test {
                val actualEffect = awaitItem()
                val expectedEffect = MovieEffect.UnknownError
                assertEquals(expectedEffect, actualEffect)

                expectNoEvents()
            }
        }

    @Test
    fun `when we get any exception while fetching data then we should log error`() = runTest {
        every { localMovieRepository.popularMovies } returns flowOf(emptyList())
        coEvery {
            movieRepository.getPopularMovies(any())
        } returns Result.failure(IllegalStateException())

        val viewModel = PopularMoviesViewModel(
            movieRepository = Lazy { movieRepository },
            localMovieRepository = Lazy { localMovieRepository },
            dataStoreRepository = Lazy { storeRepository }
        )

        viewModel.effect.test {
            awaitItem()

            expectNoEvents()
        }

        coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }
    }

    @Test
    fun `when view model get refresh event then it should reload data from network`() = runTest {
        every { localMovieRepository.popularMovies } returns flowOf(emptyList())
        coEvery { movieRepository.getPopularMovies(any()) } returns Result.success(emptyList())
        coJustRun { localMovieRepository.clearPopularMovies() }

        val viewModel = PopularMoviesViewModel(
            movieRepository = Lazy { movieRepository },
            localMovieRepository = Lazy { localMovieRepository },
            dataStoreRepository = Lazy { storeRepository }
        )

        viewModel.state.test {
            skipItems(2)
            expectNoEvents()
        }
        viewModel.sendEvent(MovieEvent.Refresh)
        viewModel.state.test {
            skipItems(1)
            expectNoEvents()
        }

        runCurrent()

        coVerify(exactly = 1) { localMovieRepository.clearPopularMovies() }
        verify(exactly = 1) { localMovieRepository.popularMovies }
        coVerify(exactly = 2) { localMovieRepository.insertPopularMovies(any()) }
        coVerify(exactly = 2) { movieRepository.getPopularMovies(any()) }
    }

    @Test
    fun `when view model get movie click event then it should save movie to favorites`() = runTest {
        every { localMovieRepository.popularMovies } returns flowOf(emptyList())
        coEvery { movieRepository.getPopularMovies(any()) } returns Result.success(emptyList())
        coJustRun { localMovieRepository.insertFavoriteMovie(any()) }

        val viewModel = PopularMoviesViewModel(
            movieRepository = Lazy { movieRepository },
            localMovieRepository = Lazy { localMovieRepository },
            dataStoreRepository = Lazy { storeRepository }
        )

        viewModel.state.test {
            awaitItem()
            awaitItem()
            expectNoEvents()
        }
        viewModel.sendEvent(
            MovieEvent.OnMovieClicked(
                MovieItem(
                    id = 1,
                    posterUrl = "Poster url",
                    title = "Title",
                    releaseDate = "Release Date",
                    rating = 24,
                    language = "PL"
                )
            )
        )
        viewModel.state.test {
            awaitItem()
            expectNoEvents()
        }
        runCurrent()

        coVerify(exactly = 1) { localMovieRepository.insertFavoriteMovie(any()) }
        verify(exactly = 1) { localMovieRepository.popularMovies }
        coVerify(exactly = 1) { localMovieRepository.insertPopularMovies(any()) }
        coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Dispatchers.setMain(StandardTestDispatcher())
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            Dispatchers.resetMain()
        }
    }
}
