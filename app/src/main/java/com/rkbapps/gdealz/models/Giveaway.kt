package com.rkbapps.gdealz.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName



@Entity(tableName = "giveaways")
data class Giveaway(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val description: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("gamerpower_url")
    val gamerPowerUrl: String,
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
    val isClaimed: Boolean = false,
)