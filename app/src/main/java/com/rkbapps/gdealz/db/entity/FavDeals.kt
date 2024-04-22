package com.rkbapps.gdealz.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav-deals")
data class FavDeals (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val dealID: String,
    val gameID: String,
    val thumb: String?,
    val title: String?
)