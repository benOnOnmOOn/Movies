package com.bz.network.di

import com.bz.network.api.service.MovieService
import com.bz.network.repository.MovieRepository
import com.bz.network.repository.MovieRepositoryImpl
import com.bz.network.utils.InternetConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {

    @Provides
    fun provideMovieRepository(
        apiService: MovieService,
        internetConnection: InternetConnection,
    ): MovieRepository = MovieRepositoryImpl(apiService, internetConnection)
}
