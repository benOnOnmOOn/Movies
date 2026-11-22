package com.bz.movies

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule

internal const val MAIN_ACTIVITY = "com.bz.movies.presentation.MainActivity"
internal const val DATABASE_NAME = "MoviesDB"
internal const val LIST_MOVIE_TITLE = "Cruella"
internal const val DETAILS_MOVIE_TITLE = "Wonder Woman 1984"
internal const val DETAILS_RELEASE_DATE = "2020-12-16"

private const val TIMEOUT_MS = 10_000L

internal fun ComposeTestRule.waitUntilShown(matcher: SemanticsMatcher) {
    waitUntil(TIMEOUT_MS) { onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty() }
}

internal fun ComposeTestRule.waitUntilGone(matcher: SemanticsMatcher) {
    waitUntil(TIMEOUT_MS) { onAllNodes(matcher).fetchSemanticsNodes().isEmpty() }
}
