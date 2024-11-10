package com.bz.movies.datastore.repository

import android.annotation.SuppressLint
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val PLAYING_NOW_KEY = "playing_now_refresh_data"
private const val POPULAR_KEY = "popular_refresh_data"

@SuppressLint("DenyListedApi")
internal class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    val playingNowKey = longPreferencesKey(PLAYING_NOW_KEY)
    val popularKey = longPreferencesKey(POPULAR_KEY)

    override suspend fun insertRefreshDatePlyingNow(data: Date) {
        dataStore.edit {
            it[playingNowKey] = data.time
        }
    }

    override suspend fun getPlyingNowRefreshDate(): Date {
        val flow: Flow<Long> = dataStore.data.map { it[playingNowKey] ?: 0L }
        return Date(flow.first())
    }

    override suspend fun insertPopularNowRefreshDate(data: Date) {
        dataStore.edit {
            it[popularKey] = data.time
        }
    }

    override suspend fun getPopularRefreshDate(): Date {
        val flow: Flow<Long> = dataStore.data.map { it[popularKey] ?: 0L }
        return Date(flow.first())
    }
}
