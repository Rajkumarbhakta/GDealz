package com.rkbapps.gdealz.models

import com.google.gson.annotations.SerializedName



data class Giveaway(
    val description: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("gamerpower_url")
    val gamerPowerUrl: String,
    val id: Int,
    val image: String,
    val instructions: String,
    @SerializedName("open_giveaway")
    val openGiveaway: String,
    @SerializedName("open_giveaway_url")
    val openGiveawayUrl: String,
    val platforms: String,
    @SerializedName("published_date")
    val publishedDate: String,
    val status: String,
    val thumbnail: String,
    val title: String,
    val type: String,
    val users: Int,
    val worth: String,
)