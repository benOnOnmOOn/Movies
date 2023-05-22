package com.bz.movies.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.SampleDAO
import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.entity.SampleEntity


private const val DATABASE_NAME = "MoviesDB"

internal fun createMoviesDatabase(
    context: Application
): MoviesDatabase =
    Room.databaseBuilder(
        context = context,
        klass = MoviesDatabase::class.java,
        name = DATABASE_NAME
    ).build()

@Database(
    entities = [SampleEntity::class, MovieEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class MoviesDatabase : RoomDatabase() {
    abstract fun sampleDAO(): SampleDAO

    abstract fun movieDAO(): MovieDAO
}

