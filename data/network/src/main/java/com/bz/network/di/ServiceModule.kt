package com.bz.network.di

import com.bz.network.api.service.CurrencyService
import com.bz.network.api.service.MovieService
import com.bz.network.di.qualifiiers.CurrencyRetrofit
import com.bz.network.di.qualifiiers.MoviesRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.create
import throwOnMainThread

@Module
@InstallIn(ViewModelComponent::class)
internal class ServiceModule {

    @Provides
    internal fun provideMoviesApiService(@MoviesRetrofit retrofit: Retrofit): MovieService {
        throwOnMainThread("provideMoviesApiService")
        return retrofit.create()
    }

    @Provides
    internal fun provideCurrencyApiService(@CurrencyRetrofit retrofit: Retrofit): CurrencyService {
        throwOnMainThread("provideCurrencyApiService")
        return retrofit.create()
    }
}
