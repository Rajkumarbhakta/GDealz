package com.rkbapps.gdealz.navigation

import kotlinx.serialization.Serializable
import okhttp3.Route


sealed class Routes {

    @Serializable
    data object Main : Routes()

    @Serializable
    data object Splash : Routes()

    @Serializable
    data object Deals : Routes()

    @Serializable
    data object Search : Routes()

    @Serializable
    data object Fav : Routes()

    @Serializable
    data object FreeDeals : Routes()

    @Serializable
    data class DealsLookup(val dealId: String?, val title: String?,val isCheapest: Boolean = false) : Routes()

    @Serializable
    data class SteamGameDetails(val steamId: String, val dealId: String?, val title: String?) : Routes()

    @Serializable
    data class FreeGameDetails(val giveaway: String) : Routes()

    @Serializable
    data class GameInfo(val gameId: String,val title: String?) : Routes()


}