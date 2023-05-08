package com.bz.network.di

import com.bz.network.api.service.MovieService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"

val apiModule = module {

    factory {
        Retrofit
            .Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    factory<MovieService> { get<Retrofit>().create(MovieService::class.java) }

}
