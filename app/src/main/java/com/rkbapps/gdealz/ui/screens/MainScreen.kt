package com.rkbapps.gdealz.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.navigation.BottomNavigationItem
import com.rkbapps.gdealz.navigation.BottomNavigationNavGraph
import com.rkbapps.gdealz.ui.tab.BottomNavigationBar


@Composable
fun MainScreen(navController: NavHostController){
    val navigationItems = listOf(
        BottomNavigationItem.Deals,
        BottomNavigationItem.Free,
        BottomNavigationItem.Search,
        BottomNavigationItem.Fav
    )
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(items = navigationItems, navController = navController)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding())
        ) {
            BottomNavigationNavGraph(
                navController = navController,
            )
        }
    }
}