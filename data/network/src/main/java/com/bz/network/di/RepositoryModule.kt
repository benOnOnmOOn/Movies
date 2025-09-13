package com.bz.network.di

import com.bz.tools.throwOnMainThread
import com.bz.network.api.service.CurrencyService
import com.bz.network.api.service.MovieService
import com.bz.network.repository.CurrencyRepository
import com.bz.network.repository.CurrencyRepositoryImpl
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.MovieRepositoryImpl
import com.bz.network.utils.InternetConnection
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal class RepositoryModule {
    @Provides
    internal fun provideNetworkMovieRepository(
        apiService: Lazy<MovieService>,
        internetConnection: Lazy<InternetConnection>
    ): MovieRepository {
        throwOnMainThread()

        return MovieRepositoryImpl(apiService, internetConnection)
    }

    @Provides
    internal fun provideNetworkCurrencyRepository(
        apiService: Lazy<CurrencyService>,
        internetConnection: Lazy<InternetConnection>
    ): CurrencyRepository {
        throwOnMainThread()

        return CurrencyRepositoryImpl(apiService, internetConnection)
    }
}
