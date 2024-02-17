package com.bz.movies.presentation.screens.popular

import app.cash.turbine.test
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.network.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PopularMoviesViewModelTest {

    private val movieRepository: MovieRepository = mockk()
    private val localMovieRepository: LocalMovieRepository = mockk(relaxed = true)

    @Test
    fun `when viewmodel is init it should load try to load data from locale storage and network`() =
        runTest {
            every { localMovieRepository.popularMovies } returns flowOf(emptyList())
            coEvery { movieRepository.getPopularMovies(any()) } returns Result.success(emptyList())

            val viewModel = PopularMoviesViewModel(movieRepository, localMovieRepository)
            viewModel.state.test {
                val actualItem = awaitItem()
                val expectedItem = MoviesState()
                assertEquals(expectedItem, actualItem)

                val actualItem2 = awaitItem()
                val expectedItem2 = MoviesState(isLoading = false, isRefreshing = false)
                assertEquals(expectedItem2, actualItem2)

                verify(exactly = 1) { localMovieRepository.popularMovies }
                coVerify(exactly = 1) { localMovieRepository.insertPopularMovies(any()) }
                coVerify(exactly = 1) { movieRepository.getPopularMovies(any()) }

                expectNoEvents()
            }
        }

    companion object {
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
