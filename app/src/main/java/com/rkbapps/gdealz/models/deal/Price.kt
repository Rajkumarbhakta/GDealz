package com.rkbapps.gdealz.models.deal

import com.google.gson.annotations.SerializedName


data class Price(

    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("amountInt") val amountInt: Int? = null,
    @SerializedName("currency") val currency: String? = null

)
