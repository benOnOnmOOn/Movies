package com.bz.movies

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

internal object MockApi {
    @Volatile
    private var server: MockWebServer? = null

    fun start() {
        server = MockWebServer().apply {
            dispatcher = TmdbDispatcher()
            start()
        }
    }

    fun shutdown() {
        server?.shutdown()
        server = null
    }

    fun redirect(url: HttpUrl): HttpUrl =
        checkNotNull(server) { "MockApi is not started" }
            .url(url.encodedPath)
            .newBuilder()
            .encodedQuery(url.encodedQuery)
            .build()
}

internal class RedirectToMockApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request.newBuilder().url(MockApi.redirect(request.url)).build())
    }
}

private val MOVIE_DETAILS_PATH = Regex(".*/movie/\\d+")

private class TmdbDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.requestUrl?.encodedPath.orEmpty()
        val fileName = when {
            path.endsWith("/movie/now_playing") -> "now_playing_movies.json"
            path.endsWith("/movie/popular") -> "popular_movies.json"
            MOVIE_DETAILS_PATH.matches(path) -> "movie_details.json"
            else -> return MockResponse().setResponseCode(404)
        }
        return MockResponse().setResponseCode(200).setBody(readApiResponse(fileName))
    }

    private fun readApiResponse(fileName: String): String =
        checkNotNull(javaClass.classLoader?.getResourceAsStream("api-response/$fileName")) {
            "Missing api-response/$fileName"
        }.bufferedReader().use { it.readText() }
}

@Module
@InstallIn(SingletonComponent::class)
internal object MockApiModule {
    @Provides
    @IntoSet
    fun provideRedirectToMockApiInterceptor(): Interceptor = RedirectToMockApiInterceptor()
}
