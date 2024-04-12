package com.rkbapps.gdealz.models

data class DealsInfo(
    val cheaperStores: List<CheaperStore>?,
    val cheapestPrice: CheapestPrice?,
    val gameInfo: GameInfo?
) {
    constructor():this(emptyList(),null,null)
}

data class CheapestPrice(
    val date: Int?,
    val price: String?
)

data class CheaperStore(
    val dealID: String?,
    val retailPrice: String?,
    val salePrice: String?,
    val storeID: String?
)

data class GameInfo(
    val gameID: String?,
    val metacriticLink: String?,
    val metacriticScore: String?,
    val name: String?,
    val publisher: String?,
    val releaseDate: Int?,
    val retailPrice: String?,
    val salePrice: String?,
    val steamAppID: String?,
    val steamRatingCount: String?,
    val steamRatingPercent: String?,
    val steamRatingText: String?,
    val steamworks: String?,
    val storeID: String?,
    val thumb: String?
)