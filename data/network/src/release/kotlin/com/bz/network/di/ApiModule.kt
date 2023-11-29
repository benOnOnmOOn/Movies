package com.bz.network.di

import android.content.Context
import com.bz.network.api.service.MovieService
import com.google.android.gms.net.CronetProviderInstaller
import com.google.android.gms.tasks.Tasks
import com.google.net.cronet.okhttptransport.CronetCallFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.themoviedb.org/3/"
const val NETWORK_CONNECTION_TIMEOUT = 30L

@Module
@InstallIn(SingletonComponent::class)
internal class ApiModule {

    @Provides
    internal fun provideCronetEngine(
        @ApplicationContext context: Context,
    ): CronetEngine {
        runBlocking {
            withContext(Dispatchers.IO) {
                Tasks.await(CronetProviderInstaller.installProvider(context))
            }
        }

        return CronetEngine.Builder(context)
            .enableBrotli(true)
            .enableQuic(true)
            .build()
    }

    @Provides
    internal fun provideCronetCallFactory(
        engine: CronetEngine
    ): CronetCallFactory =
        CronetCallFactory.newBuilder(engine).build()

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .build()

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        cornetCallFactory: CronetCallFactory
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .callFactory(cornetCallFactory)
            .baseUrl(BASE_URL)
            .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): MovieService = retrofit.create()

}
