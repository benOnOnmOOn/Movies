package com.bz.movies.database.di

import com.bz.movies.database.repository.LocalCurrencyRepository
import com.bz.movies.database.repository.LocalCurrencyRepositoryImpl
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.database.repository.LocalMovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {
    @Binds
    fun bindLocalMovieRepository(impl: LocalMovieRepositoryImpl): LocalMovieRepository

    @Binds
    fun bindLocalCurrencyRepository(impl: LocalCurrencyRepositoryImpl): LocalCurrencyRepository
}
