package com.bz.movies.datastore.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.bz.movies.datastore.repository.DataStoreRepository
import com.bz.movies.datastore.repository.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import throwOnMainThread
import timber.log.Timber

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
                    Timber.e(it)
                    emptyPreferences()
                }
            ),
            migrations = listOf(SharedPreferencesMigration(app, USER_PREFERENCES)),
            produceFile = { app.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    internal fun provideMDataStoreRepository(
        dataStore: DataStore<Preferences>
    ): DataStoreRepository {
        throwOnMainThread("provideMDataStoreRepository")
        return DataStoreRepositoryImpl(dataStore)
    }
}
