package com.bz.movies.core.di

import com.bz.movies.presentation.screens.playingNow.PlayingNowViewModel
import com.bz.movies.presentation.screens.popular.PopularMoviesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { PlayingNowViewModel(get()) }
    viewModel { PopularMoviesViewModel(get()) }
}
