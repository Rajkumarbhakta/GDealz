package com.rkbapps.gdealz.models.game_info

import com.google.gson.annotations.SerializedName


data class Players (

    @SerializedName("recent" ) val recent : Int? = null,
    @SerializedName("day"    ) val day    : Int? = null,
    @SerializedName("week"   ) val week   : Int? = null,
    @SerializedName("peak"   ) val peak   : Int? = null

)
