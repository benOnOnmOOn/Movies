package com.bz.movies.presentation.screens.details

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieItem
import com.bz.movies.presentation.screens.utils.createCustomAppWatcher
import com.bz.network.repository.EmptyBodyException
import com.bz.network.repository.HttpException
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("MainScopeUsage")
@HiltViewModel
internal class MovieDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<MovieEffect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()

    init {
        createCustomAppWatcher()
        MainScope().launch(Dispatchers.IO) {
            @Suppress("MagicNumber")
            delay(1_000_000)
            _effect.emit(MovieEffect.UnknownError)
        }
    }

    @Suppress("MagicNumber")
    fun fetchMovieDetails(movieId: Int) = viewModelScope.launch {
        val result = movieRepository.getMovieDetail(movieId)
        result.onSuccess { data ->
            _state.update {
                MovieDetailState(
                    isLoading = false,
                    movieDetails = MovieItem(
                        id = movieId,
                        language = data.language,
                        posterUrl = data.posterUrl,
                        title = data.title,
                        rating = Random.nextInt(40, 90),
                        releaseDate = data.publicationDate
                    )
                )
            }
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
            _state.update { MovieDetailState(isLoading = false) }
        }
    }
}
