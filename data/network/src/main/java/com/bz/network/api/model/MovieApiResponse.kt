package com.bz.network.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MovieApiResponse(
    val adult: Boolean?,
   @param:Json(name = "backdrop_path") val backdropPath: String?,
   @param:Json(name = "genre_ids") val genreIds: List<Int>?,
    val id: Int,
   @param:Json(name = "original_language") val originalLanguage: String?,
   @param:Json(name = "original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
   @param:Json(name = "poster_path") val posterPath: String?,
   @param:Json(name = "release_date") val releaseDate: String?,
    val title: String?,
    val video: Boolean?,
   @param:Json(name = "vote_average") val voteAverage: Double?,
   @param:Json(name = "vote_count") val voteCount: Int?
)
