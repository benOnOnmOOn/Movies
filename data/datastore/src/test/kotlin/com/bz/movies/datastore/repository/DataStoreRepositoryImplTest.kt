package com.bz.movies.datastore.repository

import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataStoreRepositoryImplTest {

    private var stored: Preferences = emptyPreferences()

    private val dataStore: DataStore<Preferences> = mockk {
        coEvery { updateData(any()) } coAnswers {
            firstArg<suspend (Preferences) -> Preferences>().invoke(stored).also { stored = it }
        }
        every { data } answers { flowOf(stored) }
    }

    private val clock = object : Clock {
        override fun now(): Instant = NOW
    }

    private val repository by lazy { DataStoreRepositoryImpl(dataStore, clock) }

    @BeforeEach
    fun setUp() {
        mockkStatic(Looper::class)
        every { Looper.myLooper() } returns mockk()
        every { Looper.getMainLooper() } returns mockk()
    }

    @AfterEach
    fun tearDown() = unmockkStatic(Looper::class)

    @Test
    fun `markPlayingNowRefreshed stores the current clock time`() = runTest {
        repository.markPlayingNowRefreshed()

        assertEquals(Result.success(NOW), repository.getPlayingNowRefreshDate())
        assertEquals(Result.success(Instant.fromEpochSeconds(0)), repository.getPopularRefreshDate())
    }

    @Test
    fun `markPopularRefreshed stores the current clock time`() = runTest {
        repository.markPopularRefreshed()

        assertEquals(Result.success(NOW), repository.getPopularRefreshDate())
        assertEquals(
            Result.success(Instant.fromEpochSeconds(0)),
            repository.getPlayingNowRefreshDate()
        )
    }

    private companion object {
        val NOW: Instant = Instant.fromEpochSeconds(1_700_000_000)
    }
}
