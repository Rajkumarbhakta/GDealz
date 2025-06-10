package com.rkbapps.gdealz.navigation

import com.rkbapps.gdealz.R

sealed class BottomNavigationItem(
    val route: Routes,
    val title: String,
    val icon: Int,
) {
    data object Deals : BottomNavigationItem(Routes.Deals, "Deals", R.drawable.shoppingcart)
    data object Search :
        BottomNavigationItem(Routes.Search, title = "Search", icon = R.drawable.search)

    data object Free :
        BottomNavigationItem(Routes.FreeDeals, title = "Free", icon = R.drawable.gift)

    data object Fav : BottomNavigationItem(Routes.Fav, title = "Fav", icon = R.drawable.fav)
}