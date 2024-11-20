package com.bz.movies.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import runSuspendCatching

private const val PLAYING_NOW_KEY = "playing_now_refresh_data"
private const val POPULAR_KEY = "popular_refresh_data"

internal class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    val playingNowKey = longPreferencesKey(PLAYING_NOW_KEY)
    val popularKey = longPreferencesKey(POPULAR_KEY)

    override suspend fun insertPlayingNowRefreshDate(data: Instant): Result<Unit> =
        runSuspendCatching { dataStore.edit { it[playingNowKey] = data.epochSecond } }

    override suspend fun getPlyingNowRefreshDate() = runSuspendCatching {
        val flow: Flow<Long> = dataStore.data.map { it[playingNowKey] ?: 0L }
        Instant.ofEpochSecond(flow.first()) ?: throw NullPointerException()
    }

    override suspend fun insertPopularNowRefreshDate(data: Instant): Result<Unit> {
        return runSuspendCatching { dataStore.edit { it[popularKey] = data.epochSecond } }
    }

    override suspend fun getPopularRefreshDate(): Result<Instant> = runSuspendCatching {
        val flow: Flow<Long> = dataStore.data.map { it[popularKey] ?: 0L }
        Instant.ofEpochSecond(flow.first()) ?: throw NullPointerException()
    }
}
