package com.bz.network.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MovieDetailsApiResponse(
    val adult: Boolean?,
    @param:Json(name = "backdrop_path") val backdropPath: String?,
    @param:Json(name = "belongs_to_collection") val belongsToCollection: BelongsToCollection?,
    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int,
    @param:Json(name = "imdb_id") val imdbId: String?,
    @param:Json(name = "original_language") val originalLanguage: String?,
    @param:Json(name = "original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    @param:Json(name = "poster_path") val posterPath: String?,
    @param:Json(name = "production_companies") val productionCompanies: List<ProductionCompany>?,
    @param:Json(name = "production_countries") val productionCountries: List<ProductionCountry>?,
    @param:Json(name = "release_date") val releaseDate: String?,
    val revenue: Int?,
    val runtime: Int?,
    @param:Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguage>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    @param:Json(name = "vote_average") val voteAverage: Double?,
    @param:Json(name = "vote_count") val voteCount: Int?
)

@JsonClass(generateAdapter = true)
internal data class BelongsToCollection(
    @param:Json(name = "backdrop_path") val backdropPath: String,
    val id: Int,
    val name: String,
    @param:Json(name = "poster_path") val posterPath: String
)

@JsonClass(generateAdapter = true)
internal data class Genre(val id: Int, val name: String)

@JsonClass(generateAdapter = true)
internal data class ProductionCompany(
    val id: Int,
    @param:Json(name = "logo_path") val logoPath: String?,
    val name: String,
    @param:Json(name = "origin_country") val originCountry: String
)

@JsonClass(generateAdapter = true)
internal data class ProductionCountry(
    @param:Json(name = "iso_3166_1") val iso31661: String,
    val name: String
)

@JsonClass(generateAdapter = true)
internal data class SpokenLanguage(
    @param:Json(name = "english_name") val englishName: String,
    @param:Json(name = "iso_639_1") val iso6391: String,
    val name: String
)
