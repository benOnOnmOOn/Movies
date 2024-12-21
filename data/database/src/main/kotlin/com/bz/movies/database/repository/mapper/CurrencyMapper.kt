package com.bz.movies.database.repository.mapper

import com.bz.dto.CurrencyDto
import com.bz.movies.database.entity.CurrencyEntity

internal fun CurrencyDto.toEntity() = CurrencyEntity(
    code = code,
    decimalDigits = decimalDigits,
    name = name,
    symbol = symbol
)

internal fun CurrencyEntity.toDto() = CurrencyDto(
    code = code,
    decimalDigits = decimalDigits,
    name = name,
    symbol = symbol
)