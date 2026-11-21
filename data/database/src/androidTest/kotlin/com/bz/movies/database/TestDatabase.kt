package com.bz.movies.database

import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry

internal fun createInMemoryDatabase(): MoviesDatabase = Room.inMemoryDatabaseBuilder(
    InstrumentationRegistry.getInstrumentation().targetContext,
    MoviesDatabase::class.java
).setDriver(AndroidSQLiteDriver())
    .build()
