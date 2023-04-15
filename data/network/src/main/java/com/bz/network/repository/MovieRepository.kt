package com.bz.network.repository

import com.bz.network.repository.model.MoveDetailDto
import com.bz.network.repository.model.PlayingNowMovieDto
import com.bz.network.repository.model.PopularMovieDto
import com.bz.network.repository.model.PopularMoviePageDto

interface MovieRepository {
    suspend fun getPlayingNowMovies(): Result<List<PlayingNowMovieDto>>

    suspend fun getPopularMovies(page: Int): Result<List<PopularMovieDto>>

    suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto>

    suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto>
}