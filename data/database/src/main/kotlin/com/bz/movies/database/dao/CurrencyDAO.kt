package com.bz.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bz.movies.database.entity.CURRENCY_ENTITY_NAME
import com.bz.movies.database.entity.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CurrencyDAO : BaseDao<CurrencyEntity> {
    @Query("SELECT * FROM $CURRENCY_ENTITY_NAME")
    fun observeAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM $CURRENCY_ENTITY_NAME")
    fun getAllCurrencies(): List<CurrencyEntity>

    @Query("DELETE FROM $CURRENCY_ENTITY_NAME")
    fun clearTable()
}
