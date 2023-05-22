package com.bz.movies.database.repository

import com.bz.movies.database.repository.model.FavoriteMovieDto

interface LocalMovieRepository {
    suspend fun getFavoritesMovies(): Result<List<FavoriteMovieDto>>

    suspend fun insertFavoriteMovie(movieDto: FavoriteMovieDto): Result<Unit>


}
