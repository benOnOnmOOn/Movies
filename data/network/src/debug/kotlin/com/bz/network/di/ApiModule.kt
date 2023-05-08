package com.bz.network.di


import com.bz.network.api.service.MovieService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"

val apiModule = module {

    factory {
        Retrofit
            .Builder()
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    factory {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    factory {
        HttpLoggingInterceptor().apply {
            setLevel(  HttpLoggingInterceptor.Level.BODY   )
        }
    }

    factory<MovieService> { get<Retrofit>().create(MovieService::class.java) }

}
