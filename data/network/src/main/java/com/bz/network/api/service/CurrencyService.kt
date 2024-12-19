package com.bz.network.api.service

import com.bz.network.api.model.CurrenciesResponseModel
import com.bz.network.api.model.ExchangeRateResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CurrencyService {

    @GET("currencies")
    suspend fun getSupportedCurrencies(
        @Query("apikey") apiKey: String
    ): Response<CurrenciesResponseModel>

    @GET("latest")
    suspend fun getExchangeRate(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String
    ): Response<ExchangeRateResponseModel>
}
