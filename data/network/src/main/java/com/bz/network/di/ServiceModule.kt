package com.bz.network.di

import com.bz.network.api.service.MovieService
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
    internal fun provideApiService(retrofit: Retrofit): MovieService {
        throwOnMainThread("provideApiService")
        return retrofit.create()
    }
}
