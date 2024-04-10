package com.rkbapps.gdealz.util

fun calculatePercentage(actualPrice: String, discountPrice: String): Int {
    return try {
        val normalPrice = if (discountPrice.isNotEmpty())actualPrice.toDouble()else 0.0
        val salePrice = if (discountPrice.isNotEmpty())discountPrice.toDouble()else 0.0
        val discount = ((salePrice * 100) / normalPrice)
        (100-discount).toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

