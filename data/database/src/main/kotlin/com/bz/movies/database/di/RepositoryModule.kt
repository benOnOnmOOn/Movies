package com.bz.movies.database.di

import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
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
    internal fun provideMovieRepository(
        movieDAO: Lazy<MovieDAO>,
        playingNowMovieDAO: Lazy<PlayingNowMovieDAO>,
        popularMovieDAO: Lazy<PopularMovieDAO>
    ): LocalMovieRepository = LocalMovieRepositoryImpl(
        movieDAO,
        playingNowMovieDAO,
        popularMovieDAO
    )
}
