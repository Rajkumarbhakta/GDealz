package com.rkbapps.gdealz.models

data class Store(
    val images: StoreImages?,
    val isActive: Int?,
    val storeID: String?,
    val storeName: String?
)

data class StoreImages(
    val banner: String?,
    val icon: String?,
    val logo: String?
)