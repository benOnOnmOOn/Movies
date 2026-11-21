package com.bz.movies.presentation.di

import com.bz.movies.presentation.utils.AppCompatLocaleSelector
import com.bz.movies.presentation.utils.LocaleSelector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {
    @Binds
    fun bindLocaleSelector(impl: AppCompatLocaleSelector): LocaleSelector
}
