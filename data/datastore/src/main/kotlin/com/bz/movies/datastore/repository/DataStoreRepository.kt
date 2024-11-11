package com.bz.movies.datastore.repository

import java.util.Date

interface DataStoreRepository {

    suspend fun insertPlayingNowRefreshDate(data: Date)

    suspend fun getPlyingNowRefreshDate(): Date

    suspend fun insertPopularNowRefreshDate(data: Date)

    suspend fun getPopularRefreshDate(): Date
}
