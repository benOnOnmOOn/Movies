package com.bz.movies

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal const val PLAYING_NOW_MOVIE_TITLE = "Fake Playing Now Movie"
internal const val POPULAR_MOVIE_TITLE = "Fake Popular Movie"
internal const val DETAILS_RELEASE_DATE = "1999-12-31"

private const val POPULAR_MOVIE_ID = 202

internal class FakeTmdbInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val body = when {
            path.endsWith("/movie/now_playing") -> NOW_PLAYING_JSON
            path.endsWith("/movie/popular") -> POPULAR_JSON
            path.endsWith("/movie/$POPULAR_MOVIE_ID") -> DETAILS_JSON
            else -> null
        }
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(if (body != null) 200 else 404)
            .message(if (body != null) "OK" else "Not Found")
            .body((body ?: "{}").toResponseBody("application/json".toMediaType()))
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal object FakeTmdbModule {
    @Provides
    @IntoSet
    fun provideFakeTmdbInterceptor(): Interceptor = FakeTmdbInterceptor()
}

private fun movieJson(id: Int, title: String) =
    """
    {
      "id": $id,
      "title": "$title",
      "original_language": "en",
      "poster_path": "/poster.jpg",
      "release_date": "2024-01-01",
      "vote_average": 7.5
    }
    """.trimIndent()

private val NOW_PLAYING_JSON =
    """
    {
      "dates": { "maximum": "2024-02-01", "minimum": "2024-01-01" },
      "page": 1,
      "results": [${movieJson(101, PLAYING_NOW_MOVIE_TITLE)}],
      "total_pages": 1,
      "total_results": 1
    }
    """.trimIndent()

private val POPULAR_JSON =
    """
    {
      "page": 1,
      "results": [${movieJson(POPULAR_MOVIE_ID, POPULAR_MOVIE_TITLE)}],
      "total_pages": 1,
      "total_results": 1
    }
    """.trimIndent()

private val DETAILS_JSON =
    """
    {
      "id": $POPULAR_MOVIE_ID,
      "title": "$POPULAR_MOVIE_TITLE",
      "original_language": "fr",
      "poster_path": "/poster.jpg",
      "release_date": "$DETAILS_RELEASE_DATE",
      "overview": "Overview",
      "genres": [{ "id": 1, "name": "Drama" }]
    }
    """.trimIndent()
