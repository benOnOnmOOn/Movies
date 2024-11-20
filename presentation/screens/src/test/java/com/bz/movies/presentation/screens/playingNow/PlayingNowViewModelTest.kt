package com.bz.movies.presentation.screens.playingNow

import app.cash.turbine.test
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.network.repository.MovieRepository
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PlayingNowViewModelTest {
    private val movieRepository: MovieRepository = mockk()
    private val localMovieRepository: LocalMovieRepository = mockk(relaxed = true)

    private val storeRepository: DataStoreRepository = mockk(relaxed = true) {
        coEvery { getPlyingNowRefreshDate() } returns Result.success(Instant.now())
    }

    @Test
    fun `when local storage is empty then it  should load data from network and store it `() =
        runTest {
            every { localMovieRepository.playingNowMovies } returns flowOf(emptyList())
            coEvery { movieRepository.getPlayingNowMovies() } returns Result.success(emptyList())

            val viewModel = PlayingNowViewModel(
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

            verify(exactly = 1) { localMovieRepository.playingNowMovies }
            coVerify(exactly = 1) { localMovieRepository.insertPlayingNowMovies(any()) }
            coVerify(exactly = 1) { movieRepository.getPlayingNowMovies() }
            coVerify(exactly = 1) { storeRepository.insertPlayingNowRefreshDate(any()) }
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
