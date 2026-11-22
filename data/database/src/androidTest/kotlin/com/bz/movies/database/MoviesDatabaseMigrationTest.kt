package com.bz.movies.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private const val TEST_DATABASE = "migration-test"

class MoviesDatabaseMigrationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(TEST_DATABASE),
        driver = AndroidSQLiteDriver(),
        databaseClass = MoviesDatabase::class
    )

    @Test
    fun migrate3To4KeepsFavoritesAndDropsOldCacheTables() = runTest {
        helper.createDatabase(3).use { connection ->
            connection.execSQL(
                "INSERT INTO MOVIE_ENTITY " +
                    "(ID, posterUrl, title, publicationDate, language, rating) " +
                    "VALUES (1, '/favorite.jpg', 'Favorite', '2024-01-01', 'en', 70)"
            )
            connection.execSQL(
                "INSERT INTO PLAYING_NOW_MOVIE_ENTITY " +
                    "(ID, posterUrl, title, publicationDate, language, rating) " +
                    "VALUES (2, '/playing.jpg', 'Playing', '2024-01-01', 'en', 60)"
            )
        }

        helper.runMigrationsAndValidate(4).use { connection ->
            assertEquals(listOf("Favorite"), connection.texts("SELECT title FROM MOVIE_ENTITY"))
            assertEquals(
                emptyList<String>(),
                connection.texts(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name IN " +
                        "('PLAYING_NOW_MOVIE_ENTITY', 'POPULAR_MOVIE_ENTITY')"
                )
            )
            assertEquals(
                emptyList<String>(),
                connection.texts("SELECT title FROM CACHED_MOVIE_ENTITY")
            )
        }
    }

    private fun SQLiteConnection.texts(sql: String): List<String> = prepare(sql).use { statement ->
        buildList {
            while (statement.step()) add(statement.getText(0))
        }
    }
}
