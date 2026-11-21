package com.bz.movies.presentation.di

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bz.movies.presentation.utils.LocaleSelector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {
    @Binds
    fun bindLocaleSelector(impl: AppCompatLocaleSelector): LocaleSelector
}

internal class AppCompatLocaleSelector @Inject constructor() : LocaleSelector {
    override fun setApplicationLocales(code: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    override fun getCurrentLang(): String? = AppCompatDelegate.getApplicationLocales()[0]?.language
}
