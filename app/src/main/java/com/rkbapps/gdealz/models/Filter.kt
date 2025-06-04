package com.rkbapps.gdealz.models

import com.rkbapps.gdealz.network.ShortingOptions

data class Filter(
    val store:Int? = null,
    val sortBy: ShortingOptions = ShortingOptions.Rating,
    val orderByDesc: Boolean = false,
    val lowerPrice: Int = 0,
    val upperPrice: Int = 80
)