package com.bz.movies.database

import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.entity.CurrencyEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyDAOTest {

    private lateinit var database: MoviesDatabase
    private lateinit var dao: CurrencyDAO

    @Before
    fun setUp() {
        database = createInMemoryDatabase()
        dao = database.currencyDAO()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertedCurrenciesGetGeneratedIds() = runTest {
        dao.insert(listOf(USD, EUR))

        val currencies = dao.getAllCurrencies()

        assertEquals(listOf("USD", "EUR"), currencies.map(CurrencyEntity::code))
        assertEquals(2, currencies.map(CurrencyEntity::id).filter { it > 0 }.toSet().size)
    }

    @Test
    fun clearTableRemovesAllCurrencies() = runTest {
        dao.insert(listOf(USD, EUR))

        dao.clearTable()

        assertEquals(emptyList<CurrencyEntity>(), dao.getAllCurrencies())
    }

    private companion object {
        val USD = CurrencyEntity(symbol = "$", name = "Dollar", decimalDigits = 2, code = "USD")
        val EUR = CurrencyEntity(symbol = "€", name = "Euro", decimalDigits = 2, code = "EUR")
    }
}
