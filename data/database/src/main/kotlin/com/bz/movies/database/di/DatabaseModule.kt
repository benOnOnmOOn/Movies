package com.bz.movies.database.di

import android.app.Application
import com.bz.movies.database.MoviesDatabase
import com.bz.movies.database.createMoviesDatabase
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.SampleDAO
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
    internal fun provideDb(app: Application): MoviesDatabase = createMoviesDatabase(app)


    @Singleton
    @Provides
    internal fun provideSomeDao(db: MoviesDatabase): SampleDAO = db.sampleDAO()

    @Singleton
    @Provides
    internal fun provideMovieDao(db: MoviesDatabase): MovieDAO = db.movieDAO()
}