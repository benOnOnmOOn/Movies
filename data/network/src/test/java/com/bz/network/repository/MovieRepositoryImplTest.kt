package com.bz.network.repository

import com.bz.dto.MovieDto
import com.bz.network.api.model.Dates
import com.bz.network.api.model.Genre
import com.bz.network.api.model.MovieApiResponse
import com.bz.network.api.model.MovieDetailsApiResponse
import com.bz.network.api.model.PlayingNowMoviesApiResponse
import com.bz.network.api.model.PopularMoviesPageApiResponse
import com.bz.network.api.service.MovieService
import com.bz.network.repository.model.MoveDetailDto
import com.bz.network.repository.model.PopularMoviePageDto
import com.bz.network.utils.InternetConnection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class MovieRepositoryImplTest {
    private val movieService: MovieService = mockk()
    private val internetConnection: InternetConnection =
        mockk {
            every { isConnected } returns true
        }
    private val movieRepository: MovieRepository =
        MovieRepositoryImpl({ movieService }, { internetConnection })

    @Test
    fun `getPlayingNowMovies return error if response is null`() = runTest {
        coEvery {
            movieService.getNowPlayingMovies(any(), any(), any())
        } returns Response.success(null)

        val result = movieRepository.getPlayingNowMovies()
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is EmptyBodyException)
        coVerify(exactly = 1) { movieService.getNowPlayingMovies(any(), any(), any()) }
    }

    @Test
    fun `getPlayingNowMovies return error if there was any exception`() = runTest {
        coEvery {
            movieService.getNowPlayingMovies(any(), any(), any())
        } throws RuntimeException()

        val result = movieRepository.getPlayingNowMovies()
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is RuntimeException)

        coVerify(exactly = 1) { movieService.getNowPlayingMovies(any(), any(), any()) }
    }

    @Test
    fun `getPlayingNowMovies return error if there http error code`() = runTest {
        coEvery {
            movieService.getNowPlayingMovies(any(), any(), any())
        } returns Response.error(400, "".toResponseBody())

        val result = movieRepository.getPlayingNowMovies()
        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue(exception is HttpException)

        coVerify(exactly = 1) { movieService.getNowPlayingMovies(any(), any(), any()) }
    }

    @Test
    fun `getPlayingNowMovies return playing now list when response is successfully and not empty`() =
        runTest {
            coEvery {
                movieService.getNowPlayingMovies(any(), any(), any())
            } returns Response.success(SUCCESS_NOW_PLAYING_MOVIES)

            val result = movieRepository.getPlayingNowMovies()
            assertTrue(result.isSuccess)

            val body = result.getOrNull()

            assertEquals(EXPECTED_NOW_PLAYING_MOVIES, body)

            coVerify(exactly = 1) { movieService.getNowPlayingMovies(any(), any(), any()) }
        }

    @Test
    fun `getPlayingNowMovies return HTTP exception list when there is no network`() = runTest {
        every { internetConnection.isConnected } returns false

        coEvery {
            movieService.getNowPlayingMovies(any(), any(), any())
        } returns Response.success(SUCCESS_NOW_PLAYING_MOVIES)

        val result = movieRepository.getPlayingNowMovies()
        assertTrue(result.isFailure)

        val excpetion = result.exceptionOrNull()

        assertTrue(excpetion is NoInternetException)

        coVerify(exactly = 0) { movieService.getNowPlayingMovies(any(), any(), any()) }
    }

    @Test
    fun `getPopularMovies return playing now list when response is successfully and not empty`() =
        runTest {
            coEvery {
                movieService.getPopularMoviePage(any(), any(), any())
            } returns Response.success(SUCCESS_POPULAR_MOVIES)

            every { internetConnection.isConnected } returns true

            val result = movieRepository.getPopularMovies(page = 1)
            assertTrue(result.isSuccess)

            val body = result.getOrNull()

            assertEquals(EXPECTED_POPULAR_MOVIES, body)

            coVerify(exactly = 1) { movieService.getPopularMoviePage(any(), any(), any()) }
        }

    @Test
    fun `getMovieDetail return playing now list when response is successfully and not empty`() =
        runTest {
            coEvery {
                movieService.getMovieDetails(any(), any(), any())
            } returns Response.success(SUCCESS_MOVIES_DETAILS)

            every { internetConnection.isConnected } returns true

            val result = movieRepository.getMovieDetail(1)
            assertTrue(result.isSuccess)

            val body = result.getOrNull()

            assertEquals(EXPECTED_MOVIE_DETAIL, body)

            coVerify(exactly = 1) { movieService.getMovieDetails(any(), any(), any()) }
        }

    @Test
    fun `getPopularMoviesPage return playing now list when response is successfully and not empty`() =
        runTest {
            coEvery {
                movieService.getPopularMoviePage(any(), any(), any())
            } returns Response.success(SUCCESS_POPULAR_MOVIES)

            every { internetConnection.isConnected } returns true

            val result = movieRepository.getPopularMoviesPage(1)
            assertTrue(result.isSuccess)

            val body = result.getOrNull()

            assertEquals(EXPECTED_POPULAR_MOVIES_PAGE, body)

            coVerify(exactly = 1) { movieService.getPopularMoviePage(any(), any(), any()) }
        }

    private companion object {
        val SUCCESS_NOW_PLAYING_MOVIES =
            PlayingNowMoviesApiResponse(
                dates = Dates(maximum = "1", minimum = "4"),
                page = 1,
                movies =
                listOf(
                    MovieApiResponse(
                        adult = true,
                        backdropPath = "path/",
                        genreIds = listOf(1, 2, 3, 4, 5),
                        id = 1,
                        originalLanguage = "Polish",
                        originalTitle = "Muminki",
                        overview = "Short and boaring",
                        popularity = 23.6,
                        posterPath = "poster/path",
                        releaseDate = "24-90-2567",
                        title = "Muminki",
                        video = false,
                        voteAverage = 4.6,
                        voteCount = 234
                    )
                ),
                totalPages = 23,
                totalResults = 234
            )

        val SUCCESS_POPULAR_MOVIES =
            PopularMoviesPageApiResponse(
                page = 1,
                movies =
                listOf(
                    MovieApiResponse(
                        adult = true,
                        backdropPath = "path/",
                        genreIds = listOf(1, 2, 3, 4, 5),
                        id = 1,
                        originalLanguage = "Polish",
                        originalTitle = "Muminki",
                        overview = "Short and boaring",
                        popularity = 23.6,
                        posterPath = "poster/path",
                        releaseDate = "24-90-2567",
                        title = "Muminki",
                        video = false,
                        voteAverage = 4.6,
                        voteCount = 234
                    )
                ),
                totalPages = 23,
                totalResults = 234
            )

        val SUCCESS_MOVIES_DETAILS =
            MovieDetailsApiResponse(
                adult = true,
                backdropPath = "path",
                belongsToCollection = null,
                budget = 1200,
                genres = listOf(Genre(1, "Movie")),
                homepage = "hompage",
                id = 1234,
                imdbId = "imdb",
                originalLanguage = "polski",
                originalTitle = "Succes",
                overview = "",
                popularity = 12.4,
                posterPath = "",
                productionCompanies = emptyList(),
                productionCountries = emptyList(),
                releaseDate = "1234",
                revenue = 123,
                runtime = 123,
                spokenLanguages = emptyList(),
                status = "empty",
                tagline = "taglinve",
                title = "title",
                video = false,
                voteAverage = 12.4,
                voteCount = 1234
            )

        val EXPECTED_NOW_PLAYING_MOVIES: List<MovieDto> =
            listOf(
                MovieDto(
                    id = 1,
                    posterUrl = "poster/path",
                    title = "Muminki",
                    publicationDate = "24-90-2567",
                    language = "Polish",
                    rating = 46
                )
            )

        val EXPECTED_MOVIE_DETAIL: MoveDetailDto =
            MoveDetailDto(
                id = 1234,
                posterUrl = "",
                publicationDate = "1234",
                language = "polski",
                title = "title",
                genre = setOf("Movie"),
                overview = ""
            )

        val EXPECTED_POPULAR_MOVIES: List<MovieDto> =
            listOf(
                MovieDto(
                    id = 1,
                    posterUrl = "poster/path",
                    title = "Muminki",
                    publicationDate = "24-90-2567",
                    language = "Polish",
                    rating = 46
                )
            )

        val EXPECTED_POPULAR_MOVIES_PAGE = PopularMoviePageDto(
            page = 1,
            totalPages = 23,
            totalResults = 234,
            popularMovies = listOf(
                MovieDto(
                    id = 1,
                    posterUrl = "poster/path",
                    title = "Muminki",
                    publicationDate = "24-90-2567",
                    language = "Polish",
                    rating = 46
                )
            )
        )
    }
}
