package com.bz.movies

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.bz.movies.presentation.screens.R as ScreensR
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()

        clickBottomItem(ScreensR.string.favorite)
        composeRule.onNodeWithText(string(ScreensR.string.your_favorite_movies)).assertIsDisplayed()
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))

        // Popular tab: tapping a movie opens its details.
        clickBottomItem(ScreensR.string.popular)
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        composeRule.waitUntilShown(hasText(DETAILS_RELEASE_DATE))
        composeRule.onNodeWithText(string(ScreensR.string.details_screen_title)).assertIsDisplayed()
        composeRule.onNodeWithText(DETAILS_MOVIE_TITLE).assertIsDisplayed()

        // Favorite tab: tapping the favorite removes it.
        clickBottomItem(ScreensR.string.favorite)
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        composeRule.waitUntilGone(hasText(LIST_MOVIE_TITLE))
    }

    private fun clickBottomItem(@StringRes label: Int) {
        composeRule.onNode(hasText(string(label)) and hasClickAction()).performClick()
    }

    private fun string(@StringRes id: Int): String = context.getString(id)
}
