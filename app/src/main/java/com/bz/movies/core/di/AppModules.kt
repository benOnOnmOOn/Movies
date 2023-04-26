package com.bz.movies.core.di

import com.bz.network.di.apiModule
import com.bz.network.di.networkModule
import com.bz.network.di.repositoryModule

val appModules = listOf(
    apiModule,
    repositoryModule,
    networkModule,
    viewModelModule
)
