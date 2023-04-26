package com.bz.network.di

import android.net.ConnectivityManager
import com.bz.network.utils.InternetConnection
import com.bz.network.utils.InternetConnectionImpl
import com.bz.network.utils.getSystemService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    factory<ConnectivityManager> { androidContext().getSystemService() }
    factory<InternetConnection> { InternetConnectionImpl(get()) }
}
