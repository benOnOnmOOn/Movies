package com.bz.movies.database.repository

import com.bz.dto.CurrencyDto
import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.entity.CurrencyEntity
import com.bz.movies.database.repository.mapper.toDto
import com.bz.movies.database.repository.mapper.toEntity
import com.bz.movies.database.runCatchingWithContext
import dagger.Lazy

public interface LocalCurrencyRepository {

    public suspend fun getAllSupportedCurrencyRepository(): Result<List<CurrencyDto>>

    public suspend fun insertAllSupportedCurrencyRepository(
        currencies: List<CurrencyDto>
    ): Result<Unit>
}

internal class LocalCurrencyRepositoryImpl(private val currencyDAO: Lazy<CurrencyDAO>) :
    LocalCurrencyRepository {
    override suspend fun getAllSupportedCurrencyRepository(): Result<List<CurrencyDto>> =
        runCatchingWithContext { currencyDAO.get().getAllCurrencies().map(CurrencyEntity::toDto) }

    override suspend fun insertAllSupportedCurrencyRepository(currencies: List<CurrencyDto>) =
        runCatchingWithContext { currencyDAO.get().insert(currencies.map(CurrencyDto::toEntity)) }
}
