package com.rkbapps.gdealz.models.game_info


import com.google.gson.annotations.SerializedName


data class Stats(

    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("waitlisted") val waitlisted: Int? = null,
    @SerializedName("collected") val collected: Int? = null

)
