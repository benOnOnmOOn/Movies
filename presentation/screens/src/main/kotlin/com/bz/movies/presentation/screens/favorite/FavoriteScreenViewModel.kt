package com.bz.movies.presentation.screens.favorite

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toDTO
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesState
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteScreenViewModel @Inject constructor(
    private val localMovieRepository: Lazy<LocalMovieRepository>
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    private val _event: MutableSharedFlow<MovieEvent> = MutableSharedFlow()
    private val event: SharedFlow<MovieEvent> = _event.asSharedFlow()

    private val _effect: Channel<MovieEffect> = Channel()
    val effect = _effect.consumeAsFlow()

    init {
        collectFavoriteMovies()
        handleEvent()
    }

    fun sendEvent(event: MovieEvent) = viewModelScope.launch(Dispatchers.IO) {
        _event.emit(event)
    }

    private fun handleEvent() = viewModelScope.launch(Dispatchers.IO) {
        event.collect { handleEvent(it) }
    }

    private suspend fun handleEvent(event: MovieEvent) {
        when (event) {
            is MovieEvent.OnMovieClicked ->
                localMovieRepository.get().deleteFavoriteMovie(event.movieItem.toDTO())

            MovieEvent.Refresh -> {
                // do nothing
            }
        }
    }

    @SuppressLint("RawDispatchersUse")
    private fun collectFavoriteMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            localMovieRepository.get().favoritesMovies
                .catch {
                    _effect.send(MovieEffect.UnknownError)
                    Logger.e("Loading issues", it)
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
