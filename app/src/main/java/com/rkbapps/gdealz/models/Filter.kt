package com.rkbapps.gdealz.models

import com.rkbapps.gdealz.network.ShortingOptions
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.LZString
import com.rkbapps.gdealz.util.json
import kotlinx.serialization.Serializable

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


    fun generateFilter():String?{

        if (lowerPrice==null&&upperPrice==null&&discount==null) return null

        val map:MutableMap<String, MinMax> = mutableMapOf()

        if (upperPrice!=null && lowerPrice!=null){
            map["price"] = MinMax(min = lowerPrice, max = upperPrice)
        }
        if (discount!=null){
            map["cut"] = MinMax(min = discount, max = 100)
        }

        val jsonString = json.encodeToString(map)
        val compressedJsonString = LZString.compressToBase64(jsonString)
        return compressedJsonString
    }






}


@Serializable
data class MinMax(
    val min:Int?=null,
    val max:Int?=null
)