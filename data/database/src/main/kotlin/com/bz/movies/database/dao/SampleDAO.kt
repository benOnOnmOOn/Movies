package com.bz.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bz.movies.database.entity.SampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SampleDAO : BaseDao<SampleEntity> {

    @Query("SELECT * FROM ${SampleEntity.ENTITY_NAME}")
    fun getAllSampleEntities(): Flow<List<SampleEntity>>

    @Query("SELECT * FROM ${SampleEntity.ENTITY_NAME}")
    suspend fun getAllSampleEntitiesSync(): List<SampleEntity>

}