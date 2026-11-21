package com.bz.movies

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.bz.movies.presentation.screens.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val MAIN_ACTIVITY = "com.bz.movies.presentation.MainActivity"
private const val DATABASE_NAME = "MoviesDB"
private const val TIMEOUT_MS = 10_000L

// First entries of now_playing_movies.json / popular_movies.json and movie_details.json,
// shared from the data:network test fixtures.
private const val LIST_MOVIE_TITLE = "Cruella"
private const val DETAILS_MOVIE_TITLE = "Wonder Woman 1984"
private const val DETAILS_RELEASE_DATE = "2020-12-16"

@HiltAndroidTest
class FavoriteMoviesFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var scenario: ActivityScenario<Activity>

    @Before
    fun setUp() {
        hiltRule.inject()
        MockApi.start()
        context.deleteDatabase(DATABASE_NAME)
        scenario = ActivityScenario.launch(
            Intent(Intent.ACTION_MAIN).setClassName(context, MAIN_ACTIVITY)
        )
    }

    @After
    fun tearDown() {
        scenario.close()
        MockApi.shutdown()
    }

    @Test
    fun addFavoriteOpenDetailsAndRemoveFavorite() {
        // Playing now is the start tab: tapping a movie there adds it to favorites.
        waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()

        clickBottomItem(R.string.favorite)
        composeRule.onNodeWithText(string(R.string.your_favorite_movies)).assertIsDisplayed()
        waitUntilShown(hasText(LIST_MOVIE_TITLE))

        // Popular tab: tapping a movie opens its details.
        clickBottomItem(R.string.popular)
        waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        waitUntilShown(hasText(DETAILS_RELEASE_DATE))
        composeRule.onNodeWithText(string(R.string.details_screen_title)).assertIsDisplayed()
        composeRule.onNodeWithText(DETAILS_MOVIE_TITLE).assertIsDisplayed()

        // Favorite tab: tapping the favorite removes it.
        clickBottomItem(R.string.favorite)
        waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        waitUntilGone(hasText(LIST_MOVIE_TITLE))
    }

    private fun clickBottomItem(@StringRes label: Int) {
        composeRule.onNode(hasText(string(label)) and hasClickAction()).performClick()
    }

    private fun waitUntilShown(matcher: SemanticsMatcher) {
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilGone(matcher: SemanticsMatcher) {
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodes(matcher).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun string(@StringRes id: Int): String = context.getString(id)
}
