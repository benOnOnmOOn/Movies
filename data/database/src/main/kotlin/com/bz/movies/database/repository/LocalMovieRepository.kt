package com.bz.movies.database.repository

import com.bz.dto.MovieDto


interface LocalMovieRepository {
    suspend fun getFavoritesMovies(): Result<List<MovieDto>>

    suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit>


}
