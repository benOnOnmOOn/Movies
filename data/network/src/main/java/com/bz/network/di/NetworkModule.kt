package com.bz.network.di

import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.bz.network.utils.InternetConnection
import com.bz.network.utils.InternetConnectionImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    factory<ConnectivityManager?> { androidContext().getSystemService<ConnectivityManager>() }
    factory<InternetConnection> { InternetConnectionImpl(get()) }
}
