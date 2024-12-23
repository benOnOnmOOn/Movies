package com.bz.movies.presentation.screens.more

import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.lruCache
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.bz.dto.CurrencyDto
import com.bz.dto.ExchangeRateDto
import com.bz.movies.database.repository.LocalCurrencyRepository
import com.bz.network.repository.CurrencyRepository
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
internal class MoreScreenViewModel @Inject constructor(
    private val currencyRepository: Lazy<CurrencyRepository>,
    private val localCurrencyRepository: Lazy<LocalCurrencyRepository>
) : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state: StateFlow<MoreState> = _state.asStateFlow()

    private val _event: MutableSharedFlow<MoreEvent> = MutableSharedFlow()
    private val event: SharedFlow<MoreEvent> = _event.asSharedFlow()

    private val lruCache = lruCache<String, List<ExchangeRateDto>>(maxSize = 2)

    init {
        collectCurrentLanguage()
        handleEvent()
        getCurrencies()
    }

    private fun getCurrencies() = viewModelScope.launch(Dispatchers.IO) {
        localCurrencyRepository.get().insertAllSupportedCurrencyRepository(
            listOf(
                CurrencyDto(symbol = "$", name = "Dollar", decimalDigits = 2, code = "USD"),
                CurrencyDto(symbol = "€", name = "Euro", decimalDigits = 2, code = "EUR"),
                CurrencyDto(symbol = "zł", name = "Polish Zloty", decimalDigits = 2, code = "PLN")
            )
        )

        val currencyRepository = currencyRepository.get()
//        currencyRepository.get().getAllCurrencies()
//        currencyRepository.get().getExchangeRate("EUR")
    }

    private fun getExchangeRate(currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val exchangeRate = currencyRepository.get().getExchangeRate(currency)
//            val exchangeRate = Result.success(
//                listOf(ExchangeRateDto("GBP", Random.nextDouble(0.5, 20.0).toFloat()))
//            )
            exchangeRate
                .onSuccess {
                    lruCache.put(currency, it)
                    _state.emit(
                        _state.value.copy(
                            exchangeRate = it.firstOrNull(),
                            selectedCurrency = currency
                        )
                    )
                }
                .onFailure {
                    _state.value.copy(exchangeRate = null)
                    Logger.e("Loading exchange error", it)
                }
        }
    }

    private fun collectCurrentLanguage() = viewModelScope.launch {
        val currentLang = AppCompatDelegate.getApplicationLocales()[0]?.language
        if (currentLang == Language.POL.code) {
            _state.emit(MoreState(Language.POL))
        } else {
            _state.emit(MoreState(Language.ENG))
        }
    }

    fun sendEvent(event: MoreEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    private fun handleEvent() = viewModelScope.launch {
        event.collect { handleEvent(it) }
    }

    private suspend fun handleEvent(event: MoreEvent) {
        when (event) {
            is MoreEvent.OnLanguageClick -> {
                val appLocale: LocaleListCompat =
                    LocaleListCompat.forLanguageTags(event.language.code)
                _state.emit(_state.value.copy(language = event.language))
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            is MoreEvent.OnCurrencyClick -> {
                Logger.i("OnCurrencyClick ${event.currency}")
                val rate = lruCache[event.currency]?.firstOrNull()
                if (rate == null) {
                    Logger.i("OnCurrencyClick getExchangeRate")
                    getExchangeRate(event.currency)
                } else {
                    Logger.i("OnCurrencyClick update state")
                    _state.emit(
                        _state.value.copy(
                            exchangeRate = rate,
                            selectedCurrency = event.currency
                        )
                    )
                }
            }
        }
    }
}
