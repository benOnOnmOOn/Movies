package com.bz.network.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CurrencyResponseModel(
    val symbol: String,
    val name: String,
    @Json(name = "symbol_native") val symbolNative: String,
    @Json(name = "decimal_digits") val decimalDigits: Int,
    val rounding: Int,
    val code: String,
    @Json(name = "name_plural") val namePlural: String,
    val type: String,
    val countries: List<String>
)

@JsonClass(generateAdapter = true)
internal data class CurrenciesResponseModel(val data: Map<String, CurrencyResponseModel>)
