package com.rkbapps.gdealz.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rkbapps.gdealz.navigation.BottomNavigationItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val itemColors = ShortNavigationBarItemDefaults.colors(
        selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColorTopIconPosition = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    ShortNavigationBar {
        items.forEachIndexed { _, bottomNavigationItem ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(route = bottomNavigationItem.route::class) } == true
            ShortNavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) ImageVector.vectorResource(bottomNavigationItem.selectedIcon) else ImageVector.vectorResource(
                            bottomNavigationItem.icon
                        ),
                        contentDescription = stringResource(id = bottomNavigationItem.title)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = bottomNavigationItem.title),
                        style = if (selected) MaterialTheme.typography.labelMediumEmphasized
                        else MaterialTheme.typography.labelMedium
                    )
                },
                colors = itemColors,

                selected = selected,
                onClick = {
                    navController.navigate(bottomNavigationItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}