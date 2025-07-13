package com.rkbapps.gdealz.models.deal

import com.google.gson.annotations.SerializedName


data class Deal(

    @SerializedName("shop") val shop: Shop? = Shop(),
    @SerializedName("price") val price: Price? = Price(),
    @SerializedName("regular") val regular: Price? = Price(),
    @SerializedName("cut") val cut: Int? = null,
    @SerializedName("voucher") val voucher: String? = null,
    @SerializedName("storeLow") val storeLow: Price? = Price(),
    @SerializedName("historyLow") val historyLow: Price? = Price(),
    @SerializedName("historyLow_1y") val historyLow1y: Price? = Price(),
    @SerializedName("historyLow_3m") val historyLow3m: Price? = Price(),
    @SerializedName("flag") val flag: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("expiry") val expiry: String? = null,
    @SerializedName("url") val url: String? = null
)