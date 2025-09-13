package com.bz.network.repository

import com.bz.dto.CurrencyDto
import com.bz.dto.ExchangeRateDto
import com.bz.network.api.model.CurrenciesResponseModel
import com.bz.network.api.model.ExchangeRateResponseModel
import com.bz.network.api.service.CurrencyService
import com.bz.network.repository.mapper.toCurrencyDto
import com.bz.network.repository.mapper.toExchangeRateDto
import com.bz.network.utils.InternetConnection
import dagger.Lazy

private const val AUTH_KEY = "cur_live_0VOoymI1vCKyBOKWLupNRIvd3LyzJABKz8V5CrhV"

public interface CurrencyRepository {
    public suspend fun getAllCurrencies(): Result<List<CurrencyDto>>

    public suspend fun getExchangeRate(baseCurrency: String): Result<List<ExchangeRateDto>>
}

internal class CurrencyRepositoryImpl(
    private val currencyService: Lazy<CurrencyService>,
    private val internetConnectionChecker: Lazy<InternetConnection>
) : CurrencyRepository {

    override suspend fun getAllCurrencies(): Result<List<CurrencyDto>> = executeApiCall(
        internetConnectionChecker.get(),
        CurrenciesResponseModel::toCurrencyDto
    ) {
        currencyService.get().getSupportedCurrencies(AUTH_KEY)
    }

    override suspend fun getExchangeRate(baseCurrency: String): Result<List<ExchangeRateDto>> =
        executeApiCall(
            internetConnectionChecker.get(),
            ExchangeRateResponseModel::toExchangeRateDto
        ) {
            currencyService.get().getExchangeRate(AUTH_KEY, baseCurrency)
        }
}
