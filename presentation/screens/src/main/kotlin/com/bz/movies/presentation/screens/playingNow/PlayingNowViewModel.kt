package com.bz.movies.presentation.screens.playingNow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MovieListSource
import com.bz.movies.presentation.screens.common.MoviesListDelegate
import com.bz.network.repository.MovieRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PlayingNowViewModel @Inject constructor(
    movieRepository: Lazy<MovieRepository>,
    localMovieRepository: Lazy<LocalMovieRepository>,
    dataStoreRepository: Lazy<DataStoreRepository>
) : ViewModel() {
    private val delegate = MoviesListDelegate(
        scope = viewModelScope,
        localMovieRepository = localMovieRepository,
        source = object : MovieListSource {
            override val cachedMovies get() = localMovieRepository.get().playingNowMovies

            override suspend fun fetchMovies() = movieRepository.get().getPlayingNowMovies()

            override suspend fun cacheMovies(movies: List<MovieDto>) =
                localMovieRepository.get().insertPlayingNowMovies(movies)

            override suspend fun clearCache() = localMovieRepository.get().clearPlayingNowMovies()

            override suspend fun markRefreshed() =
                dataStoreRepository.get().markPlayingNowRefreshed()

            override suspend fun lastRefreshDate() =
                dataStoreRepository.get().getPlayingNowRefreshDate()
        }
    )

    val state = delegate.state

    val effect = delegate.effect

    fun sendEvent(event: MovieEvent) = delegate.sendEvent(event)
}
