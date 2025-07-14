package com.rkbapps.gdealz.models

import com.rkbapps.gdealz.network.ShortingOptions
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions

data class Filter(
    val store:Int? = null,
    val sortBy: ShortingOptions = ShortingOptions.Rating,
    val orderByDesc: Boolean = false,
    val lowerPrice: Int = 0,
    val upperPrice: Int = 80
)

data class IsThereAnyDealFilters(
    val stores: List<Int> = emptyList(),
    val sort: IsThereAnyDealSortingOptions = IsThereAnyDealSortingOptions.TRENDING,
    val lowerPrice: Int? = null,
    val upperPrice: Int? = null,
    val discount:Int? = null
){
    fun generateDealFilter(
        lowerPrice: Int? = null,
        upperPrice: Int? = null,
        discount: Int? = null
    ): String? {
        val filters = mutableListOf<String>()

        // Add price filter
        when {
            lowerPrice != null && upperPrice != null -> {
                filters.add("price:$lowerPrice-$upperPrice")
            }
            lowerPrice != null -> {
                filters.add("price:>$lowerPrice")
            }
            upperPrice != null -> {
                filters.add("price:<$upperPrice")
            }
        }

        // Add discount filter
        discount?.let {
            if (it in 0..100) {
                filters.add("cut:>$it")
            }
        }

        return filters.takeIf { it.isNotEmpty() }?.joinToString(",")
    }
}