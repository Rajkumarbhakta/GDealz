package com.rkbapps.gdealz.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rkbapps.gdealz.ui.screens.MainScreen
import com.rkbapps.gdealz.ui.screens.dealslookup.DealLookupScreen
import com.rkbapps.gdealz.ui.screens.free_game_details.FreeGameDetailsScreen
import com.rkbapps.gdealz.ui.screens.splash.SplashScreen
import com.rkbapps.gdealz.ui.tab.fav.FavTab
import com.rkbapps.gdealz.ui.tab.deals.DealsTab
import com.rkbapps.gdealz.ui.tab.free.FreeDealsTab
import com.rkbapps.gdealz.ui.tab.search.SearchTab


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Splash) {
        destinations(navController)
    }

}

@Composable
fun BottomNavigationNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Deals,
    ) {
        destinations(navController)
    }
}


fun NavGraphBuilder.destinations(navController: NavHostController) {
    composable<Routes.Splash> {
        SplashScreen(navController)
    }
    composable<Routes.Deals> {
        DealsTab(navController)
    }
    composable<Routes.Search> {
        SearchTab(navController)
    }
    composable<Routes.Fav> {
        FavTab(navController)
    }
    composable<Routes.FreeDeals> {
        FreeDealsTab(navController)
    }

    composable<Routes.Main> {
        MainScreen(navController)
    }

    composable<Routes.DealsLookup> {
        DealLookupScreen(navController)
    }

    composable<Routes.FreeGameDetails> {
        FreeGameDetailsScreen(navController)
    }


}

