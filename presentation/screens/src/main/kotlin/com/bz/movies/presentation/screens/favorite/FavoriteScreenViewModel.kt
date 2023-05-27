package com.bz.movies.presentation.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toDTO
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.utils.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteScreenViewModel @Inject constructor(
    private val localMovieRepository: LocalMovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    private val _event: MutableSharedFlow<MovieEvent> = MutableSharedFlow()
    private val event: SharedFlow<MovieEvent> = _event.asSharedFlow()

    private val _effect: MutableSharedFlow<MovieEffect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()

    init {
        fetchFavoriteScreen()
        handleEvent()
    }

    fun sendEvent(event: MovieEvent) = launch {
        _event.emit(event)
    }

    private fun handleEvent() = viewModelScope.launch {
        event.collect { handleEvent(it) }
    }

    private suspend fun handleEvent(event: MovieEvent) {
        when (event) {
            is MovieEvent.OnMovieClicked ->
                localMovieRepository.deleteFavoriteMovie(event.movieItem.toDTO())
        }
    }

    private fun fetchFavoriteScreen() = viewModelScope.launch {

        localMovieRepository.favoritesMovies.collectLatest { movieDtoList ->
            _state.update {
                MoviesState(
                    isLoading = false,
                    playingNowMovies = movieDtoList.map(MovieDto::toMovieItem)
                )
            }
        }

//        result.onSuccess { data ->
//            _state.update {
//                MoviesState(
//                    isLoading = false,
//                    playingNowMovies = data.map(MovieDto::toMovieItem)
//                )
//            }
//        }
//        result.onFailure {
//            Timber.e(it)
//            _state.update { MoviesState(isLoading = false) }
//        }

    }
}

