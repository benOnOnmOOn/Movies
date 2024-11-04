package com.bz.movies.presentation.screens.more

internal data class MoreState(
    val language: Language = Language.ENG
)

internal enum class Language(val code: String) {
    POL("pl"),
    ENG("en")
}

internal sealed class MoreEvent {
    data class OnLanguageClick(val language: Language) : MoreEvent()
}
