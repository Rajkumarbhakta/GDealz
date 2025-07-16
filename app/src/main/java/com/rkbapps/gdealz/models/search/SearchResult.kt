package com.rkbapps.gdealz.models.search

import com.google.gson.annotations.SerializedName
import com.rkbapps.gdealz.models.deal.Assets

data class SearchResult(
    @SerializedName("id") var id: String,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("mature") var mature: Boolean? = null,
    @SerializedName("assets") var assets: Assets? = Assets()
)