package com.bz.movies.presentation.screens.popular

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toDTO
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.network.repository.EmptyBodyException
import com.bz.network.repository.HttpException
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
internal class PopularMoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val localMovieRepository: LocalMovieRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    private val _event: MutableSharedFlow<MovieEvent> = MutableSharedFlow()
    private val event: SharedFlow<MovieEvent> = _event.asSharedFlow()

    private val _effect: MutableSharedFlow<MovieEffect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()

    init {
        collectPopularMovies()
        handleEvent()
    }

    fun sendEvent(event: MovieEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    private fun handleEvent() = viewModelScope.launch {
        event.collect { handleEvent(it) }
    }

    private suspend fun handleEvent(event: MovieEvent) {
        when (event) {
            is MovieEvent.OnMovieClicked ->
                localMovieRepository.insertFavoriteMovie(event.movieItem.toDTO())

            MovieEvent.Refresh -> {
                localMovieRepository.clearPopularMovies()
                fetchPopularNowMovies()
            }
        }
    }

    private fun fetchPopularNowMovies() = viewModelScope.launch {
        val result = movieRepository.getPopularMovies(1)
        result.onSuccess { data ->
            _state.update {
                MoviesState(
                    isLoading = false,
                    playingNowMovies = data.map(MovieDto::toMovieItem)
                )
            }
            localMovieRepository.insertPopularMovies(data)
        }
        result.onFailure {
            val error =
                when (it) {
                    is NoInternetException, is HttpException, is EmptyBodyException ->
                        MovieEffect.NetworkConnectionError

                    else -> MovieEffect.UnknownError
                }
            _effect.emit(error)
            Timber.e(it)
            _state.update { MoviesState(isLoading = false) }
        }
    }

    @SuppressLint("RawDispatchersUse")
    private fun collectPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            localMovieRepository.popularMovies
                .flowOn(Dispatchers.Main)
                .onStart { fetchPopularNowMovies() }
                .catch {
                    _effect.emit(MovieEffect.UnknownError)
                    Timber.e(it)
                    _state.update {
                        MoviesState(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .collectLatest { data ->
                    _state.update {
                        MoviesState(
                            isLoading = false,
                            playingNowMovies = data.map(MovieDto::toMovieItem)
                        )
                    }
                }
        }
    }
}
