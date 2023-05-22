package com.bz.movies.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = SampleEntity.ENTITY_NAME,
    indices = [Index(SampleEntity.COLUMN_ID)]
)
internal data class SampleEntity(
    @PrimaryKey @ColumnInfo(name = COLUMN_ID) val id: Long,
) {

    companion object {
        internal const val ENTITY_NAME = "SAMPLE_ENTITY"
        internal const val COLUMN_ID = "ID"
    }
}