package com.rkbapps.gdealz.models.price

import com.google.gson.annotations.SerializedName
import com.rkbapps.gdealz.models.deal.Price
import com.rkbapps.gdealz.models.deal.Shop

data class PriceHistoryEntry(

    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("shop") val shop: Shop? = Shop(),
    @SerializedName("deal") val deal: PriceHistoryDeal? = null

)

data class PriceHistoryDeal(

    @SerializedName("price") val price: Price? = Price(),
    @SerializedName("regular") val regular: Price? = Price(),
    @SerializedName("cut") val cut: Int? = null

)
