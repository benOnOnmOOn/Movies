package com.bz.movies.database

import android.app.Application
import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.DeleteTable
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.AutoMigrationSpec
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.bz.movies.database.dao.CachedMovieDAO
import com.bz.movies.database.dao.CurrencyDAO
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.entity.CachedMovieEntity
import com.bz.movies.database.entity.CurrencyEntity
import com.bz.movies.database.entity.MovieEntity

private const val DATABASE_NAME = "MoviesDB"

internal fun createMoviesDatabase(context: Application): MoviesDatabase = Room.databaseBuilder(
    context = context,
    klass = MoviesDatabase::class.java,
    name = DATABASE_NAME
).setDriver(AndroidSQLiteDriver())
    .fallbackToDestructiveMigrationOnDowngrade(true)
    .build()

@DeleteTable(tableName = "PLAYING_NOW_MOVIE_ENTITY")
@DeleteTable(tableName = "POPULAR_MOVIE_ENTITY")
internal class MergeCachedMoviesMigration : AutoMigrationSpec

@Database(
    entities = [
        MovieEntity::class,
        CachedMovieEntity::class,
        CurrencyEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = MergeCachedMoviesMigration::class)
    ]
)
internal abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDAO

    abstract fun cachedMovieDAO(): CachedMovieDAO

    abstract fun currencyDAO(): CurrencyDAO
}
