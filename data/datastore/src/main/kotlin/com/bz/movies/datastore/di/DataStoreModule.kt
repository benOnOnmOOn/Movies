package com.bz.movies.datastore.di

import android.app.Application
import android.throwOnMainThread
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import co.touchlab.kermit.Logger
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.datastore.repository.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

internal const val USER_PREFERENCES = "Settings"

@Module
@InstallIn(SingletonComponent::class)
internal class DataStoreModule {
    @Singleton
    @Provides
    internal fun providePreferences(app: Application): DataStore<Preferences> {
        throwOnMainThread("providePreferences")
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = {
                    Logger.e("DataStoreModule", it)
                    emptyPreferences()
                }
            ),
            migrations = listOf(SharedPreferencesMigration(app, USER_PREFERENCES)),
            produceFile = { app.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    internal fun provideDataStoreRepository(
        dataStore: DataStore<Preferences>
    ): DataStoreRepository {
        throwOnMainThread("provideDataStoreRepository")
        return DataStoreRepositoryImpl(dataStore)
    }
}
