package com.rkbapps.gdealz.models

data class GameLookUp(
    val cheapestPriceEver: CheapestPriceEver?,
    val deals: List<Deal>?,
    val info: Info?
)

data class CheapestPriceEver(
    val date: Int?,
    val price: String?
)

data class Deal(
    val dealID: String?,
    val price: String?,
    val retailPrice: String?,
    val savings: String?,
    val storeID: String?
)


data class Info(
    val steamAppID: String?,
    val thumb: String?,
    val title: String?
)