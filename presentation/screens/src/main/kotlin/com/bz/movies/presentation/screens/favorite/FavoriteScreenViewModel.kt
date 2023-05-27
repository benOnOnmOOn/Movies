package com.bz.movies.presentation.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.dto.MovieDto
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.presentation.mappers.toMovieItem
import com.bz.movies.presentation.screens.common.MoviesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteScreenViewModel @Inject constructor(
    private val localMovieRepository: LocalMovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state.asStateFlow()

    init {
        fetchFavoriteScreen()
    }

    private fun fetchFavoriteScreen() = viewModelScope.launch {

        val result = localMovieRepository.getFavoritesMovies()

        result.onSuccess { data ->
            _state.update {
                MoviesState(
                    isLoading = false,
                    playingNowMovies = data.map(MovieDto::toMovieItem)
                )
            }
        }
        result.onFailure {
            Timber.e(it)
            _state.update { MoviesState(isLoading = false) }
        }

    }
}

