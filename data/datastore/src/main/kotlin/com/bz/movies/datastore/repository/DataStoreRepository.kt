package com.bz.movies.datastore.repository

import kotlin.time.Instant

public interface DataStoreRepository {

    public suspend fun markPlayingNowRefreshed(): Result<Unit>

    public suspend fun getPlayingNowRefreshDate(): Result<Instant>

    public suspend fun markPopularRefreshed(): Result<Unit>

    public suspend fun getPopularRefreshDate(): Result<Instant>
}
