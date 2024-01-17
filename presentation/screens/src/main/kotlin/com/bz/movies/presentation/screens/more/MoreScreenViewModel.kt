package com.bz.movies.presentation.screens.more

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bz.movies.presentation.utils.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MoreScreenViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state: StateFlow<MoreState> = _state.asStateFlow()

    private val _event: MutableSharedFlow<MoreEvent> = MutableSharedFlow()
    private val event: SharedFlow<MoreEvent> = _event.asSharedFlow()

    init {
        collectCurrentLanguage()
        handleEvent()
    }

    private fun collectCurrentLanguage() = launch {
        val currentLang = AppCompatDelegate.getApplicationLocales()[0]?.language
        if (currentLang == Language.POL.code) {
            _state.emit(MoreState(Language.POL))
        } else {
            _state.emit(MoreState(Language.ENG))
        }
    }

    fun sendEvent(event: MoreEvent) = launch {
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
                _state.emit(MoreState(event.language))
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }
    }
}
