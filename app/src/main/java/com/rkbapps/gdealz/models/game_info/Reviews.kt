package com.rkbapps.gdealz.models.game_info

import com.google.gson.annotations.SerializedName


data class Reviews(

    @SerializedName("score") val score: Int? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("url") val url: String? = null

)
