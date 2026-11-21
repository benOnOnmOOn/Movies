package com.bz.movies.presentation.screens.common

import android.annotation.SuppressLint
import co.touchlab.kermit.Logger
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toDTO
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.network.repository.EmptyBodyException
import com.bz.network.repository.HttpException
import com.bz.network.repository.NoInternetException
import dagger.Lazy
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoviesListDelegate(
    private val scope: CoroutineScope,
    private val localMovieRepository: Lazy<LocalMovieRepository>,
    private val cachedMovies: () -> Flow<List<MovieDto>>,
    private val fetchMovies: suspend () -> Result<List<MovieDto>>,
    private val cacheMovies: suspend (List<MovieDto>) -> Result<Unit>,
    private val clearCache: suspend () -> Result<Unit>,
    private val markRefreshed: suspend () -> Result<Unit>,
    private val lastRefreshDate: suspend () -> Result<Instant>
) {
    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    private val _effect: Channel<MovieEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        collectMovies()
    }

    fun sendEvent(event: MovieEvent) = scope.launch(Dispatchers.IO) {
        handleEvent(event)
    }

    private suspend fun handleEvent(event: MovieEvent) {
        when (event) {
            is MovieEvent.OnMovieClicked ->
                localMovieRepository.get().insertFavoriteMovie(event.movieItem.toDTO())

            MovieEvent.Refresh -> {
                clearCache()
                _state.update {
                    it.copy(
                        isLoading = true,
                        isRefreshing = true
                    )
                }
                refreshMovies()
            }
        }
    }

    private fun refreshMovies() = scope.launch(Dispatchers.IO) {
        val result = fetchMovies()
        result.onSuccess { data ->
            markRefreshed()
            cacheMovies(data)
        }
        result.onFailure {
            val error =
                when (it) {
                    is NoInternetException, is HttpException, is EmptyBodyException ->
                        MovieEffect.NetworkConnectionError

                    else -> MovieEffect.UnknownError
                }
            _effect.send(error)
            Logger.e("Loading error", it)
            _state.update { moviesState ->
                moviesState.copy(
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    @SuppressLint("RawDispatchersUse")
    private fun collectMovies() {
        scope.launch(Dispatchers.IO) {
            lastRefreshDate()
                .onFailure { Logger.e("Loading error", it) }
                .onSuccess { Logger.d("Last date : $it") }

            cachedMovies()
                .flowOn(Dispatchers.Main)
                .onStart { refreshMovies() }
                .catch {
                    _effect.send(MovieEffect.UnknownError)
                    Logger.e("Loading error", it)
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
                            isRefreshing = false,
                            playingNowMovies = data.map(MovieDto::toMovieItem)
                        )
                    }
                }
        }
    }
}
