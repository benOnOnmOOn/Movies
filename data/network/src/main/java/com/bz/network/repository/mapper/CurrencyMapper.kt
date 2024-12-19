package com.bz.network.repository.mapper

import com.bz.dto.CurrencyDto
import com.bz.dto.ExchangeRateDto
import com.bz.network.api.model.CurrenciesResponseModel
import com.bz.network.api.model.CurrencyResponseModel
import com.bz.network.api.model.ExchangeRate
import com.bz.network.api.model.ExchangeRateResponseModel

internal fun CurrencyResponseModel.toCurrencyDto() = CurrencyDto(
    symbol = symbol,
    name = name,
    decimalDigits = decimalDigits,
    code = code
)

internal fun CurrenciesResponseModel.toCurrencyDto(): List<CurrencyDto> =
    data.values.map(CurrencyResponseModel::toCurrencyDto)

internal fun ExchangeRateResponseModel.toExchangeRateDto(): List<ExchangeRateDto> =
    data.values.map(ExchangeRate::toExchangeRateDto)

internal fun ExchangeRate.toExchangeRateDto() = ExchangeRateDto(
    code = code,
    exchangeRate = value
)
