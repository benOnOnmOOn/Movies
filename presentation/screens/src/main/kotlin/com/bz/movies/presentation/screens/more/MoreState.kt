package com.bz.movies.presentation.screens.more

import com.bz.dto.ExchangeRateDto

internal data class MoreState(
    val language: Language = Language.ENG,
    val exchangeRate: ExchangeRateDto? = null,
    val selectedCurrency: String? = null
)

internal enum class Language(val code: String) {
    POL("pl"),
    ENG("en")
}

internal sealed class MoreEvent {
    data class OnLanguageClick(val language: Language) : MoreEvent()
    data class OnCurrencyClick(val currency: String) : MoreEvent()
}
