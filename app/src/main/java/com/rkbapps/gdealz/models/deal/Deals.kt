package com.rkbapps.gdealz.models.deal

import com.google.gson.annotations.SerializedName


data class Deals(

    @SerializedName("nextOffset") val nextOffset: Int? = null,
    @SerializedName("hasMore") val hasMore: Boolean? = null,
    @SerializedName("list") val list: ArrayList<DealItem> = arrayListOf()

)