package com.bz.movies.database.dao

import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy

internal interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: List<T>)

    @Delete
    suspend fun delete(obj: T)
}
