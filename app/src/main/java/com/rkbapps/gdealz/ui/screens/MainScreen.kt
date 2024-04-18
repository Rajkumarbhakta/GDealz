package com.rkbapps.gdealz.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rkbapps.gdealz.navigation.BottomNavigationItem
import com.rkbapps.gdealz.navigation.BottomNavigationNavgraph
import com.rkbapps.gdealz.ui.tab.BottomNavigation

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val navController = rememberNavController()
        val navigationItems = listOf(
            BottomNavigationItem.Deals,
            BottomNavigationItem.Free,
            BottomNavigationItem.Search,
            BottomNavigationItem.Fav
        )
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                BottomNavigation(items = navigationItems, navController = navController)
            }
        ) {
            BottomNavigationNavgraph(
                navController = navController,
                innerPadding = it,
                navigator = navigator
            )
        }


    }
}