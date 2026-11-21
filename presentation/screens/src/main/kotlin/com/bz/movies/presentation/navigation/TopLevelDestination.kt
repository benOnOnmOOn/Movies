package com.bz.movies.presentation.navigation

import com.bz.movies.presentation.screens.R

internal data class TopLevelDestination(
    val route: Route,
    val unselectedIcon: Int,
    val selectedIcon: Int,
    val iconTextId: Int
)

internal val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = PlayingNowRoute,
            unselectedIcon = R.drawable.ic_playing_now,
            selectedIcon = R.drawable.ic_playing_now,
            iconTextId = R.string.playing_now
        ),
        TopLevelDestination(
            route = PopularGraph,
            unselectedIcon = R.drawable.ic_popular,
            selectedIcon = R.drawable.ic_popular,
            iconTextId = R.string.popular
        ),
        TopLevelDestination(
            route = FavoriteRoute,
            unselectedIcon = R.drawable.ic_favorite,
            selectedIcon = R.drawable.ic_favorite,
            iconTextId = R.string.favorite
        ),
        TopLevelDestination(
            route = MoreRoute,
            unselectedIcon = R.drawable.ic_more,
            selectedIcon = R.drawable.ic_more,
            iconTextId = R.string.more
        )
    )
