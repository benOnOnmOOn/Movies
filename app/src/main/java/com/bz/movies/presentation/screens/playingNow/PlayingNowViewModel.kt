package com.bz.movies.presentation.screens.playingNow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.presentation.mappers.toPlayingNowMovieItem
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.model.PlayingNowMovieDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PlayingNowViewModel(
    private val movieRepository: MovieRepository
) : ViewModel(), KoinComponent {


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
                    playingNowMovies = data.map(PlayingNowMovieDto::toPlayingNowMovieItem)
                )
            }
        }
        result.onFailure {
            _state.update { PlayingNowState(isLoading = false) }
        }

    }
}

