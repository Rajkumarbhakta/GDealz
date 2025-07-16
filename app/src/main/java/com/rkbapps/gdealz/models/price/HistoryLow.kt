package com.rkbapps.gdealz.models.price

import com.google.gson.annotations.SerializedName
import com.rkbapps.gdealz.models.deal.Price

data class HistoryLow(
    @SerializedName("all") val all: Price? = Price(),
    @SerializedName("y1") val y1: Price? = Price(),
    @SerializedName("m3") val m3: Price? = Price()
)
