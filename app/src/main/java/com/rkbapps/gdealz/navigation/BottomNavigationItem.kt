package com.rkbapps.gdealz.navigation

import com.rkbapps.gdealz.R

sealed class BottomNavigationItem(
    val route: String,
    val title: String,
    val icon: Int,
) {
    data object Deals : BottomNavigationItem("deals", "Deals", R.drawable.shoppingcart)
    data object Search : BottomNavigationItem(route = "search", title = "Search", icon = R.drawable.search)
    data object Free : BottomNavigationItem(route = "free", title = "Free", icon = R.drawable.gift)
    data object Fav : BottomNavigationItem(route = "fav", title = "Fav", icon = R.drawable.fav)
//    object Profile : BottomNavigationItem(
//        route = "profile",
//        title = "Profile",
//        icon = R.drawable.accountuserpersonround
//    )
}