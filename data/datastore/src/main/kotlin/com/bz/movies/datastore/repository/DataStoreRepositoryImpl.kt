package com.bz.movies.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.bz.tools.runSuspendCatching
import com.bz.tools.throwOnMainThread
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val PLAYING_NOW_KEY = "playing_now_refresh_data"
private const val POPULAR_KEY = "popular_refresh_data"

internal class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val clock: Clock
) : DataStoreRepository {
    init {
        throwOnMainThread()
    }

    private val playingNowKey = longPreferencesKey(PLAYING_NOW_KEY)
    private val popularKey = longPreferencesKey(POPULAR_KEY)

    override suspend fun markPlayingNowRefreshed(): Result<Unit> = runSuspendCatching {
        dataStore.edit { it[playingNowKey] = clock.now().epochSeconds }
    }

    override suspend fun getPlayingNowRefreshDate(): Result<Instant> = runSuspendCatching {
        Instant.fromEpochSeconds(dataStore.data.map { it[playingNowKey] ?: 0L }.first())
    }

    override suspend fun markPopularRefreshed(): Result<Unit> = runSuspendCatching {
        dataStore.edit { it[popularKey] = clock.now().epochSeconds }
    }

    override suspend fun getPopularRefreshDate(): Result<Instant> = runSuspendCatching {
        Instant.fromEpochSeconds(dataStore.data.map { it[popularKey] ?: 0L }.first())
    }
}
