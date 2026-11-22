package com.bz.movies.presentation.screens.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.presentation.di.IoDispatcher
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesListDelegate
import com.bz.network.repository.MovieRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
internal class PopularMoviesViewModel @Inject constructor(
    movieRepository: Lazy<MovieRepository>,
    localMovieRepository: Lazy<LocalMovieRepository>,
    dataStoreRepository: Lazy<DataStoreRepository>,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val delegate = MoviesListDelegate(
        scope = viewModelScope,
        ioDispatcher = ioDispatcher,
        localMovieRepository = localMovieRepository,
        cachedMovies = { localMovieRepository.get().popularMovies },
        fetchMovies = { movieRepository.get().getPopularMovies(1) },
        cacheMovies = { localMovieRepository.get().insertPopularMovies(it) },
        clearCache = { localMovieRepository.get().clearPopularMovies() },
        markRefreshed = { dataStoreRepository.get().markPopularRefreshed() },
        lastRefreshDate = { dataStoreRepository.get().getPopularRefreshDate() }
    )

    val state = delegate.state

    val effect = delegate.effect

    fun sendEvent(event: MovieEvent) = delegate.sendEvent(event)
}
