package com.bz.movies.presentation.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
internal fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.surface
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            val isItemSelected =
                currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
            NavigationBarItem(
                selected = isItemSelected,
                onClick = { navController.navigateToTopLevel(destination.route) },
                icon = { NavIcon(destination, isItemSelected) },
                alwaysShowLabel = true,
                label = {
                    Text(
                        text = stringResource(id = destination.iconTextId),
                        color = getNavTextColor(isItemSelected)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun getNavTextColor(isItemSelected: Boolean): Color = if (isItemSelected) {
    Color.Red
} else {
    MaterialTheme.colorScheme.onBackground
}

@Composable
private fun NavIcon(destination: TopLevelDestination, isItemSelected: Boolean) = Icon(
    painter = painterResource(
        id = if (isItemSelected) destination.selectedIcon else destination.unselectedIcon
    ),
    contentDescription = stringResource(id = destination.iconTextId),
    tint = if (isItemSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
)
