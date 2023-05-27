package com.bz.movies.presentation.screens.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.network.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val localMovieRepository: LocalMovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    init {
        fetchPopularNowMovies()

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
        }
        result.onFailure {
            _state.update { MoviesState(isLoading = false) }
        }

    }

}

