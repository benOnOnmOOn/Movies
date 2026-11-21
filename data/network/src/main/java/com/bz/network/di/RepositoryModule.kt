package com.bz.network.di

import com.bz.network.repository.CurrencyRepository
import com.bz.network.repository.CurrencyRepositoryImpl
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {
    @Binds
    fun bindNetworkMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    fun bindNetworkCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository
}
