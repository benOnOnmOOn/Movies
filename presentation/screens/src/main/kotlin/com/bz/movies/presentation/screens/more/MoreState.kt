package com.bz.movies.presentation.screens.more


data class MoreState(
    val language: Language = Language.ENG
)

enum class Language(val code:String) {
    POL("pl"),
    ENG("en")
}

sealed class MoreEvent {
    data class OnLanguageClick(val language: Language) : MoreEvent()
}
