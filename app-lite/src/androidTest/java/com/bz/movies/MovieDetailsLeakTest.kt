package com.bz.movies

import android.app.Activity
import android.content.Intent
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
import leakcanary.NoLeakAssertionFailedError
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Test(expected = NoLeakAssertionFailedError::class)
    fun movieDetailsViewModelIsNotLeakedAfterLeavingDetails() {
        composeRule.onNode(
            hasText(context.getString(ScreensR.string.popular)) and hasClickAction()
        ).performClick()
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.onNodeWithText(LIST_MOVIE_TITLE).performClick()
        composeRule.waitUntilShown(hasText(DETAILS_RELEASE_DATE))

        Espresso.pressBack()
        composeRule.waitUntilShown(hasText(LIST_MOVIE_TITLE))
        composeRule.waitUntilGone(hasText(DETAILS_RELEASE_DATE))
        composeRule.waitForIdle()

        LeakAssertions.assertNoLeaks()
    }
}
