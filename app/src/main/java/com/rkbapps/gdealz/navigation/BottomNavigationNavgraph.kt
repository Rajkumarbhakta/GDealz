package com.rkbapps.gdealz.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cafe.adriel.voyager.navigator.Navigator
import com.rkbapps.gdealz.ui.tab.FavTab
import com.rkbapps.gdealz.ui.tab.home.HomeTab
import com.rkbapps.gdealz.ui.tab.SearchTab

@Composable
fun BottomNavigationNavgraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    navigator: Navigator
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = BottomNavigationItem.Home.route) {
            HomeTab(navigator = navigator)
        }
        composable(route = BottomNavigationItem.Search.route) {
            SearchTab(navigator = navigator)
        }
        composable(route = BottomNavigationItem.Fav.route) {
            FavTab(navigator = navigator)
        }
    }
}



