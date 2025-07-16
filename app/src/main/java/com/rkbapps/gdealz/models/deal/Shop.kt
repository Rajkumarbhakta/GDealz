package com.rkbapps.gdealz.models.deal

import com.google.gson.annotations.SerializedName

data class Shop(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null

)
