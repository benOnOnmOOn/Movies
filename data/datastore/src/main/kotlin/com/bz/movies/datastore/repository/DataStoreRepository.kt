package com.bz.movies.datastore.repository

import java.time.Instant

public interface DataStoreRepository {

    public suspend fun insertPlayingNowRefreshDate(data: Instant): Result<Unit>

    public suspend fun getPlyingNowRefreshDate(): Result<Instant>

    public suspend fun insertPopularNowRefreshDate(data: Instant): Result<Unit>

    public suspend fun getPopularRefreshDate(): Result<Instant>
}
