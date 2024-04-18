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
import com.rkbapps.gdealz.ui.tab.deals.HomeTab
import com.rkbapps.gdealz.ui.tab.search.SearchTab
import com.rkbapps.gdealz.ui.tab.free.FreeDealsTab

@Composable
fun BottomNavigationNavgraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    navigator: Navigator
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.Deals.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = BottomNavigationItem.Deals.route) {
            HomeTab(navigator = navigator)
        }
        composable(route = BottomNavigationItem.Search.route) {
            SearchTab(navigator = navigator)
        }
        composable(route = BottomNavigationItem.Fav.route) {
            FavTab(navigator = navigator)
        }
        composable(route = BottomNavigationItem.Free.route) {
            FreeDealsTab(navigator = navigator)
        }
    }
}



