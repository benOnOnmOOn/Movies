package com.bz.movies.presentation.screens.common

internal sealed class MovieEffect {
    internal data object NetworkConnectionError : MovieEffect()

    internal data object UnknownError : MovieEffect()
}
