package com.bz.movies.database.di

import android.app.Application
import com.bz.tools.throwOnMainThread
import com.bz.movies.database.MoviesDatabase
import com.bz.movies.database.createMoviesDatabase
import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    @Singleton
    @Provides
    internal fun provideDb(app: Application): MoviesDatabase {
        throwOnMainThread()
        return createMoviesDatabase(app)
    }

    @Singleton
    @Provides
    internal fun provideMovieDao(db: MoviesDatabase): MovieDAO {
        throwOnMainThread()
        return db.movieDAO()
    }

    @Singleton
    @Provides
    internal fun providePlayingNowMovieDao(db: MoviesDatabase): PlayingNowMovieDAO {
        throwOnMainThread()
        return db.playingNowMovieDAO()
    }

    @Singleton
    @Provides
    internal fun providePopularMovieDao(db: MoviesDatabase): PopularMovieDAO {
        throwOnMainThread()
        return db.popularMovieDAO()
    }

    @Singleton
    @Provides
    internal fun provideCurrencyDao(db: MoviesDatabase): CurrencyDAO {
        throwOnMainThread()
        return db.currencyDAO()
    }
}
