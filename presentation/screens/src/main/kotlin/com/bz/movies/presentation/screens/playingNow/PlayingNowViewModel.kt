package com.bz.movies.presentation.screens.playingNow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.network.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayingNowViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayingNowState())
    val state: StateFlow<PlayingNowState> = _state.asStateFlow()

    init {
        fetchPlayingNowMovies()
    }

    private fun fetchPlayingNowMovies() = viewModelScope.launch {

        val result = movieRepository.getPlayingNowMovies()

        result.onSuccess { data ->
            _state.update {
                PlayingNowState(
                    isLoading = false,
                    playingNowMovies = data.map(MovieDto::toMovieItem)
                )
            }
        }
        result.onFailure {
            Timber.e(it)
            _state.update { PlayingNowState(isLoading = false) }
        }

    }
}

