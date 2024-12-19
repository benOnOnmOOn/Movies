package com.bz.network.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ExchangeRateResponseModel(
    val meta: ExchangeMetaData,
    val data: Map<String, ExchangeRate>
)

@JsonClass(generateAdapter = true)
internal data class ExchangeMetaData(
    @Json(name = "last_updated_at") val lastUpdatedAt: String
)

@JsonClass(generateAdapter = true)
internal data class ExchangeRate(
    val code: String,
    val value: Float
)
