package com.bz.network.api

import com.bz.network.api.model.CurrenciesResponseModel
import com.bz.network.api.model.CurrencyResponseModel
import com.bz.network.api.model.ExchangeMetaData
import com.bz.network.api.model.ExchangeRate
import com.bz.network.api.model.ExchangeRateResponseModel
import com.bz.network.api.service.CurrencyService
import com.bz.network.utils.enqueueFromFile
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CurrencyApiTest {
    private val mockWebServer = MockWebServer()

    private val retrofit =
        Retrofit
            .Builder()
            .client(OkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(mockWebServer.url("/"))
            .build()

    private val currencyService: CurrencyService = retrofit.create(CurrencyService::class.java)

    @Test
    fun `getSupportedCurrencies check json parsing using sample json`() = runTest {
        mockWebServer.enqueueFromFile("supported_currencies.json")

        val response = currencyService.getSupportedCurrencies(apiKey = "api-key")
        assertTrue(response.isSuccessful)

        assertTrue(response.body() is CurrenciesResponseModel)
        val body = response.body()
        assertEquals(SUPPORTED_CURRENCIES, body)
    }

    @Test
    fun `getExchangeRate check json parsing using sample json`() = runTest {
        mockWebServer.enqueueFromFile("exchange_rate.json")

        val response = currencyService.getExchangeRate(apiKey = "api-key", baseCurrency = "USD")
        assertTrue(response.isSuccessful)

        assertTrue(response.body() is ExchangeRateResponseModel)
        val body = response.body()
        assertEquals(EXPECTED_EXCHANGE_RATE, body)
    }

    @Test
    fun `getSupportedCurrencies check if json is correctly parsed for huge real data`() = runTest {
        mockWebServer.enqueueFromFile("all_supported_currencies.json")

        val response = currencyService.getSupportedCurrencies(apiKey = "api-key")
        assertTrue(response.isSuccessful)

        assertTrue(response.body() is CurrenciesResponseModel)
//        val body = response.body()
//        assertEquals(SUPPORTED_CURRENCIES, body)
    }

    @Test
    fun `getExchangeRate check if json is correctly parsed for huge real data`() = runTest {
        mockWebServer.enqueueFromFile("all_exchange_rate.json")

        val response = currencyService.getExchangeRate(apiKey = "api-key", baseCurrency = "EUR")
        assertTrue(response.isSuccessful)

        assertTrue(response.body() is ExchangeRateResponseModel)
//        val body = response.body()
//        assertEquals(EXPECTED_EXCHANGE_RATE, body)
    }

    private companion object {
        private val SUPPORTED_CURRENCIES = CurrenciesResponseModel(
            data = mapOf(
                "AED" to CurrencyResponseModel(
                    symbol = "AED",
                    name = "United Arab Emirates Dirham",
                    symbolNative = "د.إ",
                    decimalDigits = 2,
                    rounding = 0,
                    code = "AED",
                    namePlural = "UAE dirhams",
                    type = "fiat",
                    countries = listOf("AE")
                ),
                "AFN" to CurrencyResponseModel(
                    symbol = "Af",
                    name = "Afghan Afghani",
                    symbolNative = "؋",
                    decimalDigits = 0,
                    rounding = 0,
                    code = "AFN",
                    namePlural = "Afghan Afghanis",
                    type = "fiat",
                    countries = listOf("AF")
                )
            )
        )

        private val EXPECTED_EXCHANGE_RATE = ExchangeRateResponseModel(
            meta = ExchangeMetaData(lastUpdatedAt = "2023-06-23T10:15:59Z"),
            data = mapOf(
                "AED" to ExchangeRate(code = "AED", value = 3.67306f),
                "AFN" to ExchangeRate(code = "AFN", value = 91.80254f),
                "ALL" to ExchangeRate(code = "ALL", value = 108.22904f),
                "AMD" to ExchangeRate(code = "AMD", value = 480.41659f)
            )
        )
    }
}
