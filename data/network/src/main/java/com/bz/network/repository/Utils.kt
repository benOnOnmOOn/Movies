package com.bz.network.repository

import android.runSuspendCatching
import com.bz.network.utils.InternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal suspend inline fun <T, R> executeApiCall(
    internetConnectionChecker: InternetConnection,
    crossinline mapper: (T) -> R,
    crossinline apiCall: suspend () -> Response<T>
): Result<R> = withContext(Dispatchers.IO) {
    if (!internetConnectionChecker.isConnected) {
        return@withContext Result.failure(NoInternetException())
    }

    runSuspendCatching {
        val response = apiCall()

        if (response.isSuccessful) {
            response
                .body()
                ?.let { apiResponse -> mapper(apiResponse) }
                ?: throw EmptyBodyException()
        } else {
            throw HttpException(response.message())
        }
    }
}
