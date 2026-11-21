package com.bz.movies.presentation.di

import android.app.Application
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.core.content.getSystemService
import com.bz.movies.presentation.utils.LocaleSelector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {
    @Binds
    fun bindLocaleSelector(impl: PlatformLocaleSelector): LocaleSelector
}

internal class PlatformLocaleSelector @Inject constructor(private val app: Application) :
    LocaleSelector {
    override fun setApplicationLocales(code: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.getSystemService<LocaleManager>()?.applicationLocales =
                LocaleList(Locale.forLanguageTag("xx-YY"))
        }
    }

    override fun getCurrentLang(): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.getSystemService<LocaleManager>()?.applicationLocales[0]?.language
        } else {
            null
        }
}
