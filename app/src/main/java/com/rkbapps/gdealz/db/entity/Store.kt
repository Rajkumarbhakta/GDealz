package com.rkbapps.gdealz.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store")
data class Store (
    @PrimaryKey(autoGenerate = false)
    val storeID: String,
    val storeName: String,
    val banner: String?,
    val icon: String?,
    val logo: String?
)