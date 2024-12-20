package com.bz.movies.database.di

import android.app.Application
import com.bz.movies.database.MoviesDatabase
import com.bz.movies.database.createMoviesDatabase
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.throwOnMainThread

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    @Singleton
    @Provides
    internal fun provideDb(app: Application): MoviesDatabase {
        throwOnMainThread("provideDb")
        return createMoviesDatabase(app)
    }

    @Singleton
    @Provides
    internal fun provideMovieDao(db: MoviesDatabase): MovieDAO {
        throwOnMainThread("provideMovieDao")
        return db.movieDAO()
    }

    @Singleton
    @Provides
    internal fun providePlayingNowMovieDao(db: MoviesDatabase): PlayingNowMovieDAO {
        throwOnMainThread("providePlayingNowMovieDao")
        return db.playingNowMovieDAO()
    }

    @Singleton
    @Provides
    internal fun providePopularMovieDao(db: MoviesDatabase): PopularMovieDAO {
        throwOnMainThread("providePopularMovieDao")
        return db.popularMovieDAO()
    }
}
