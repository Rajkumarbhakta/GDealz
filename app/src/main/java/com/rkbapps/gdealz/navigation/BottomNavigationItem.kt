package com.rkbapps.gdealz.navigation

import com.rkbapps.gdealz.R

sealed class BottomNavigationItem(
    val route: Routes,
    val title: String,
    val icon: Int,
    val selectedIcon:Int
) {
    data object Deals : BottomNavigationItem(Routes.Deals, "Deals",
        R.drawable.cart_outlined,R.drawable.cart_filled)
    data object Search :
        BottomNavigationItem(Routes.Search, title = "Search", icon = R.drawable.search, selectedIcon = R.drawable.search)

    data object Free :
        BottomNavigationItem(Routes.FreeDeals, title = "Free", icon = R.drawable.gift_outlined, selectedIcon = R.drawable.gift_filled)

    data object Fav : BottomNavigationItem(Routes.Fav, title = "Fav", icon = R.drawable.fav_outlined,R.drawable.fav_filled)
}