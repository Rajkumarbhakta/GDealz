package com.rkbapps.gdealz.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rkbapps.gdealz.R

sealed class BottomNavigationItem(
    val route: Routes,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    data object Deals : BottomNavigationItem(
        Routes.Deals, R.string.tab_deals,
        R.drawable.cart_outlined, R.drawable.cart_filled
    )

    data object Search :
        BottomNavigationItem(
            Routes.Search,
            title = R.string.search,
            icon = R.drawable.search,
            selectedIcon = R.drawable.search
        )

    data object Free :
        BottomNavigationItem(
            Routes.FreeDeals,
            title = R.string.free_title,
            icon = R.drawable.gift_outlined,
            selectedIcon = R.drawable.gift_filled
        )

    data object Fav : BottomNavigationItem(
        Routes.Fav,
        title = R.string.tab_fav,
        icon = R.drawable.fav_outlined,
        R.drawable.fav_filled
    )

    data object Settings : BottomNavigationItem(
        Routes.Settings,
        title = R.string.settings,
        icon = R.drawable.settings_outline,
        selectedIcon = R.drawable.settings_filled
    )
}