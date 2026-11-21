package com.bz.movies.presentation.screens.popular

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
internal class PopularMoviesViewModel @Inject constructor(
    movieRepository: Lazy<MovieRepository>,
    localMovieRepository: Lazy<LocalMovieRepository>,
    dataStoreRepository: Lazy<DataStoreRepository>
) : ViewModel() {
    private val delegate = MoviesListDelegate(
        scope = viewModelScope,
        localMovieRepository = localMovieRepository,
        source = object : MovieListSource {
            override val cachedMovies get() = localMovieRepository.get().popularMovies

            override suspend fun fetchMovies() = movieRepository.get().getPopularMovies(1)

            override suspend fun cacheMovies(movies: List<MovieDto>) =
                localMovieRepository.get().insertPopularMovies(movies)

            override suspend fun clearCache() = localMovieRepository.get().clearPopularMovies()

            override suspend fun markRefreshed() = dataStoreRepository.get().markPopularRefreshed()

            override suspend fun lastRefreshDate() =
                dataStoreRepository.get().getPopularRefreshDate()
        }
    )

    val state = delegate.state

    val effect = delegate.effect

    fun sendEvent(event: MovieEvent) = delegate.sendEvent(event)
}
