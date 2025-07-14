package com.rkbapps.gdealz.models.price

import com.google.gson.annotations.SerializedName
import com.rkbapps.gdealz.models.deal.Price
import com.rkbapps.gdealz.models.deal.Shop


data class Deals(

    @SerializedName("shop") val shop: Shop? = Shop(),
    @SerializedName("price") val price: Price? = Price(),
    @SerializedName("regular") val regular: Price? = Price(),
    @SerializedName("cut") val cut: Int? = null,
    @SerializedName("voucher") val voucher: String? = null,
    @SerializedName("storeLow") val storeLow: Price? = Price(),
    @SerializedName("flag") val flag: String? = null,
    @SerializedName("drm") val drm: ArrayList<Drm> = arrayListOf(),
    @SerializedName("platforms") val platforms: ArrayList<Platforms> = arrayListOf(),
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("expiry") val expiry: String? = null,
    @SerializedName("url") val url: String? = null

)
