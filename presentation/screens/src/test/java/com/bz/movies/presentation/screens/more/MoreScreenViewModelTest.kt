package com.bz.movies.presentation.screens.more

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import app.cash.turbine.test
import com.bz.dto.ExchangeRateDto
import com.bz.movies.database.repository.LocalCurrencyRepository
import com.bz.network.repository.CurrencyRepository
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MoreScreenViewModelTest {

    private val currencyRepository: CurrencyRepository = mockk()
    private val localCurrencyRepository: LocalCurrencyRepository = mockk(relaxed = true)

    @Test
    fun `when view model is init then it should load all currencies from currency storage`() =
        runTest {
            coJustRun { localCurrencyRepository.insertAllSupportedCurrencyRepository(any()) }
            val viewModel = MoreScreenViewModel(
                currencyRepository = Lazy { currencyRepository },
                localCurrencyRepository = Lazy { localCurrencyRepository }
            )

            viewModel.state.test {
                val actualItem = awaitItem()
                val expectedItem = MoreState()
                assertEquals(expectedItem, actualItem)

                expectNoEvents()
            }

            coVerify(exactly = 1) {
                localCurrencyRepository.insertAllSupportedCurrencyRepository(any())
            }
            coVerify(exactly = 0) { localCurrencyRepository.getAllSupportedCurrencyRepository() }
            coVerify(exactly = 0) { currencyRepository.getAllCurrencies() }
        }

    @Test
    fun `when view model get change currency event it should fetch exchange rate`() =
        runTest {
            coJustRun { localCurrencyRepository.insertAllSupportedCurrencyRepository(any()) }
            coEvery {
                currencyRepository.getExchangeRate("USD")
            } returns Result.success(
                listOf(
                    ExchangeRateDto("EUR", 124.0f),
                    ExchangeRateDto("GBP", 1.21340f),
                    ExchangeRateDto("PLN", 32.0f),
                )
            )
            val viewModel = MoreScreenViewModel(
                currencyRepository = Lazy { currencyRepository },
                localCurrencyRepository = Lazy { localCurrencyRepository }
            )


            viewModel.sendEvent(MoreEvent.OnCurrencyClick("USD"))

            viewModel.state.test {
                skipItems(1)
                val actualItem = awaitItem()
                val expectedItem = MoreState(
                    selectedCurrency = "USD",
                    exchangeRate = ExchangeRateDto("EUR", 124.0f),
                )
                assertEquals(expectedItem, actualItem)

                expectNoEvents()
            }

            coVerify(exactly = 1) {
                localCurrencyRepository.insertAllSupportedCurrencyRepository(any())
            }
            coVerify(exactly = 0) { localCurrencyRepository.getAllSupportedCurrencyRepository() }
            coVerify(exactly = 1) { currencyRepository.getExchangeRate(any()) }
        }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            Dispatchers.setMain(StandardTestDispatcher())
            mockkStatic(AppCompatDelegate::getApplicationLocales)
            every {
                AppCompatDelegate.getApplicationLocales()
            } returns LocaleListCompat.getEmptyLocaleList()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            unmockkStatic(AppCompatDelegate::getApplicationLocales)
            Dispatchers.resetMain()
        }
    }

}