package com.rkbapps.gdealz.navigation

import com.rkbapps.gdealz.models.Giveaway
import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object Main : Routes()

    @Serializable
    data object Splash : Routes()

    @Serializable
    data object Home : Routes()

    @Serializable
    data object Search : Routes()

    @Serializable
    data object Fav : Routes()

    @Serializable
    data object FreeDeals : Routes()

    @Serializable
    data class DealsLookup(
        val dealId: String?,
        val title:String?
    ) : Routes()

    @Serializable
    data class FreeGameDetails(
        val giveaway: String,
    ):Routes()


}