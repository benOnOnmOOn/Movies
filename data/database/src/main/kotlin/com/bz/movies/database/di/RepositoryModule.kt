package com.bz.movies.database.di

import android.throwOnMainThread
import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
import com.bz.movies.database.repository.LocalCurrencyRepository
import com.bz.movies.database.repository.LocalCurrencyRepositoryImpl
import com.bz.movies.database.repository.LocalMovieRepository
import com.bz.movies.database.repository.LocalMovieRepositoryImpl
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal class RepositoryModule {
    @Provides
    internal fun provideLocalMovieRepository(
        movieDAO: Lazy<MovieDAO>,
        playingNowMovieDAO: Lazy<PlayingNowMovieDAO>,
        popularMovieDAO: Lazy<PopularMovieDAO>
    ): LocalMovieRepository {
        throwOnMainThread()

        return LocalMovieRepositoryImpl(
            movieDAO,
            playingNowMovieDAO,
            popularMovieDAO
        )
    }

    @Provides
    internal fun provideCurrencyRepository(
       currencyDAO: Lazy<CurrencyDAO>
    ): LocalCurrencyRepository {
        throwOnMainThread()

        return LocalCurrencyRepositoryImpl(currencyDAO)
    }
}
