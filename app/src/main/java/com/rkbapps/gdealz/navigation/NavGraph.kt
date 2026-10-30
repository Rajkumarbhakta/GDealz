package com.rkbapps.gdealz.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.rkbapps.gdealz.ui.screens.MainScreen
import com.rkbapps.gdealz.ui.screens.free_game_details.FreeGameDetailsScreen
import com.rkbapps.gdealz.ui.screens.free_game_details.FreeGameDetailsScreenExpressive
import com.rkbapps.gdealz.ui.screens.game_info.GameInfoScreen
import com.rkbapps.gdealz.ui.screens.game_info.GameInfoScreenExpressive
import com.rkbapps.gdealz.ui.screens.image_preview.ImagePreviewScreen
import com.rkbapps.gdealz.ui.screens.image_preview.ScreenshotViewerScreen
import com.rkbapps.gdealz.ui.tab.settings.SettingsScreen
import com.rkbapps.gdealz.ui.tab.settings.SettingsScreenExpressive
import com.rkbapps.gdealz.ui.screens.splash.SplashScreen
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.IsThereAnyDealSteamDetailsPage
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.IsThereAnyDealSteamDetailsPageExpressive
import com.rkbapps.gdealz.ui.tab.deals.DealsTab
import com.rkbapps.gdealz.ui.tab.fav.FavTab
import com.rkbapps.gdealz.ui.tab.fav.FavTabExpressive
import com.rkbapps.gdealz.ui.tab.deals.DealsTabExpressive
import com.rkbapps.gdealz.ui.tab.free.FreeDealsTab
import com.rkbapps.gdealz.ui.tab.free.FreeDealsTabExpressive
import com.rkbapps.gdealz.ui.tab.search.SearchTab
import com.rkbapps.gdealz.ui.tab.search.SearchTabExpressive
import com.rkbapps.gdealz.ui.theme.LocalClassicUi


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            enterTransition = navEnterTransition,
            exitTransition = navExitTransition,
            popEnterTransition = navPopEnterTransition,
            popExitTransition = navPopExitTransition,
            predictivePopEnterTransition = navPredictivePopEnterTransition,
            predictivePopExitTransition = navPredictivePopExitTransition,
        ) {
            destinations(navController, this@SharedTransitionLayout)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BottomNavigationNavGraph(
    navController: NavHostController,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.Deals,
            enterTransition = navEnterTransition,
            exitTransition = navExitTransition,
            popEnterTransition = navPopEnterTransition,
            popExitTransition = navPopExitTransition,
            predictivePopEnterTransition = navPredictivePopEnterTransition,
            predictivePopExitTransition = navPredictivePopExitTransition,
        ) {
            destinations(navController, this@SharedTransitionLayout)
        }
    }
}


fun NavGraphBuilder.destinations(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope
) {
    composable<Routes.Splash> {
        SplashScreen(navController)
    }
    composable<Routes.Deals> {
        if (LocalClassicUi.current) DealsTab(navController) else DealsTabExpressive(navController)
    }
    composable<Routes.Search> {
        if (LocalClassicUi.current) SearchTab(navController) else SearchTabExpressive(navController)
    }
    composable<Routes.Fav> {
        if (LocalClassicUi.current) FavTab(navController) else FavTabExpressive(navController)
    }
    composable<Routes.FreeDeals> {
        if (LocalClassicUi.current) FreeDealsTab(navController) else FreeDealsTabExpressive(navController)
    }

    composable<Routes.Main> {
        MainScreen(navController)
    }

    composable<Routes.FreeGameDetails>(
        deepLinks = listOf(
            navDeepLink <Routes.FreeGameDetails>(basePath = DeepLinkBasePaths.FREE_GAME_DETAILS)
        )
    ) {
        if (LocalClassicUi.current) {
            FreeGameDetailsScreen(navController)
        } else {
            FreeGameDetailsScreenExpressive(navController)
        }
    }

    composable<Routes.GameInfo> {
        if (LocalClassicUi.current) GameInfoScreen(navController) else GameInfoScreenExpressive(navController)
    }

    composable<Routes.IsThereAnyDealSteamGameDetails>(
        deepLinks = listOf(navDeepLink<Routes.IsThereAnyDealSteamGameDetails>(basePath = DeepLinkBasePaths.GAME_DETAILS))
    ) {
        if (LocalClassicUi.current) {
            IsThereAnyDealSteamDetailsPage(
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = this
            )
        } else {
            IsThereAnyDealSteamDetailsPageExpressive(
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = this
            )
        }
    }

    composable<Routes.Settings> {
        if (LocalClassicUi.current) SettingsScreen(navController) else SettingsScreenExpressive(navController)
    }

    composable<Routes.ScreenshotViewer> {
        val route = it.toRoute<Routes.ScreenshotViewer>()
        ScreenshotViewerScreen(
            navController = navController,
            imageUrls = route.imageUrls,
            startIndex = route.startIndex,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this
        )
    }

    composable<Routes.ImagePreview> {
        val imageUrl = it.toRoute<Routes.ImagePreview>().imageUrl
        ImagePreviewScreen(
            navController = navController,
            imageUrl = imageUrl,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = this
        )
    }


}

