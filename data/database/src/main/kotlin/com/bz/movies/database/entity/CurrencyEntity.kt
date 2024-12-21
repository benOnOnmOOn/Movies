package com.bz.movies.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

internal const val CURRENCY_ENTITY_NAME = "CURRENCY_ENTITY"

@Entity(tableName = CURRENCY_ENTITY_NAME)
internal data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val symbol: String,
    val name: String,
    val decimalDigits: Int,
    val code: String
)
