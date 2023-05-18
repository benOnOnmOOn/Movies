package com.bz.movies.presentation.screens.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.presentation.mappers.toPopularMovieItem
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.model.PopularMovieDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PopularMoviesState())
    val state: StateFlow<PopularMoviesState> = _state.asStateFlow()

    init {
        fetchPopularNowMovies()
    }

    private fun fetchPopularNowMovies() = viewModelScope.launch {

        val result = movieRepository.getPopularMovies(1)

        result.onSuccess { data ->
            _state.update {
                PopularMoviesState(
                    isLoading = false,
                    playingNowMovies = data.map(PopularMovieDto::toPopularMovieItem)
                )
            }
        }
        result.onFailure {
            _state.update { PopularMoviesState(isLoading = false) }
        }

    }

}

