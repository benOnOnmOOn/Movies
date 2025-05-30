package com.bz.movies.database

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
import com.bz.movies.database.entity.CurrencyEntity
import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.entity.PlayingNowMovieEntity
import com.bz.movies.database.entity.PopularMovieEntity

private const val DATABASE_NAME = "MoviesDB"

internal fun createMoviesDatabase(context: Application): MoviesDatabase = Room.databaseBuilder(
    context = context,
    klass = MoviesDatabase::class.java,
    name = DATABASE_NAME
).fallbackToDestructiveMigrationOnDowngrade(true)
    .build()

@Database(
    entities = [
        MovieEntity::class,
        PlayingNowMovieEntity::class,
        PopularMovieEntity::class,
        CurrencyEntity::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
internal abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDAO

    abstract fun playingNowMovieDAO(): PlayingNowMovieDAO

    abstract fun popularMovieDAO(): PopularMovieDAO

    abstract fun currencyDAO(): CurrencyDAO
}
