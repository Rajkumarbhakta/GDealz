package com.rkbapps.gdealz.models

import com.google.gson.annotations.SerializedName
import java.util.UUID


data class Deals(
    val key: String = "${UUID.randomUUID()}",
    val dealID: String?,
    val dealRating: String?,
    val gameID: String?,
    val internalName: String?,
    val isOnSale: String?,
    val lastChange: Int?,
    @SerializedName("metacriticLink")
    val metaCriticPageUrl: String?,
    @SerializedName("metacriticScore")
    val metaCriticScore: String?,
    val normalPrice: String?,
    val releaseDate: Int?,
    val salePrice: String?,
    val savings: String?,
    val steamAppID: String?,
    val steamRatingCount: String?,
    val steamRatingPercent: String?,
    val steamRatingText: String?,
    val storeID: String?,
    val thumb: String?,
    val title: String?
)