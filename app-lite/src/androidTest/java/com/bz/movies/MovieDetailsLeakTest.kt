package com.bz.movies

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.bz.movies.presentation.screens.R as ScreensR
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import leakcanary.LeakAssertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val MAIN_ACTIVITY = "com.bz.movies.presentation.MainActivity"
private const val DATABASE_NAME = "MoviesDB"
private const val TIMEOUT_MS = 10_000L
private const val LIST_MOVIE_TITLE = "Cruella"
private const val DETAILS_RELEASE_DATE = "2020-12-16"

@HiltAndroidTest
class MovieDetailsLeakTest {

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
    fun movieDetailsViewModelIsNotLeakedAfterLeavingDetails() {
        composeRule.onNode(
            hasText(context.getString(ScreensR.string.popular)) and hasClickAction()
        ).performClick()
        waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        waitUntilShown(hasText(DETAILS_RELEASE_DATE))

        Espresso.pressBack()
        waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodes(hasText(DETAILS_RELEASE_DATE)).fetchSemanticsNodes().isEmpty()
        }
        composeRule.waitForIdle()

        LeakAssertions.assertNoLeaks()
    }

    private fun waitUntilShown(matcher: SemanticsMatcher) {
        composeRule.waitUntil(TIMEOUT_MS) {
            composeRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
