package com.bz.movies.presentation.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bz.movies.presentation.utils.LocaleSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class CoreModule {
    @Provides
    internal fun provideDb(app: Application): LocaleSelector = object : LocaleSelector {
        override fun setApplicationLocales(code: String) {
            val appLocale: LocaleListCompat =
                LocaleListCompat.forLanguageTags(code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        override fun getCurrentLang(): String? =
            AppCompatDelegate.getApplicationLocales()[0]?.language
    }
}
