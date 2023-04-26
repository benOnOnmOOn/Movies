package com.bz.network.di

import com.bz.network.repository.MovieRepository
import com.bz.network.repository.MovieRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    factory<MovieRepository> { MovieRepositoryImpl(get(), get()) }
}
