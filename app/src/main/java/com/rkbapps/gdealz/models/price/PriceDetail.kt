package com.rkbapps.gdealz.models.price

import com.google.gson.annotations.SerializedName


data class PriceDetail(

    @SerializedName("id") val id: String? = null,
    @SerializedName("historyLow") val historyLow: HistoryLow? = HistoryLow(),
    @SerializedName("deals") val deals: ArrayList<Deals> = arrayListOf()

)