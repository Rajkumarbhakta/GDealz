package com.rkbapps.gdealz.models.deal


import com.google.gson.annotations.SerializedName


data class Assets(

    @SerializedName("boxart") var boxart: String? = null,
    @SerializedName("banner145") var banner145: String? = null,
    @SerializedName("banner300") var banner300: String? = null,
    @SerializedName("banner400") var banner400: String? = null,
    @SerializedName("banner600") var banner600: String? = null

)
