package com.bz.movies.presentation.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import javax.inject.Inject

internal class AppCompatLocaleSelector @Inject constructor() : LocaleSelector {
    override fun setApplicationLocales(code: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    override fun getCurrentLang(): String? = AppCompatDelegate.getApplicationLocales()[0]?.language
}
