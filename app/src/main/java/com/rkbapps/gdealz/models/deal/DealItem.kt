package com.rkbapps.gdealz.models.deal

import com.google.gson.annotations.SerializedName


data class DealItem(

    @SerializedName("id") val id: String,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("mature") val mature: Boolean? = null,
    @SerializedName("assets") val assets: Assets? = Assets(),
    @SerializedName("deal") val deal: Deal? = Deal()

)