package com.bz.movies.presentation.screens.playingNow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.presentation.screens.common.MovieEvent
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
        cachedMovies = { localMovieRepository.get().playingNowMovies },
        fetchMovies = { movieRepository.get().getPlayingNowMovies() },
        cacheMovies = { localMovieRepository.get().insertPlayingNowMovies(it) },
        clearCache = { localMovieRepository.get().clearPlayingNowMovies() },
        markRefreshed = { dataStoreRepository.get().markPlayingNowRefreshed() },
        lastRefreshDate = { dataStoreRepository.get().getPlayingNowRefreshDate() }
    )

    val state = delegate.state

    val effect = delegate.effect

    fun sendEvent(event: MovieEvent) = delegate.sendEvent(event)
}
