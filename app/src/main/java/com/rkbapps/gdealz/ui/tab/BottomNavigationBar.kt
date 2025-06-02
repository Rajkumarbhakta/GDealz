package com.rkbapps.gdealz.ui.tab



import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rkbapps.gdealz.navigation.BottomNavigationItem

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val destination = items.any { currentDestination?.hasRoute(route = it.route::class)==true }
    if (destination) {
        BottomNavigation {
            items.forEachIndexed { _, bottomNavigationItem ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(bottomNavigationItem.icon),
                            contentDescription = bottomNavigationItem.title
                        )
                    },
                    label = { Text(bottomNavigationItem.title) },
                    selected = currentDestination?.hierarchy?.any { it.hasRoute(route = bottomNavigationItem.route::class) } == true,
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
}