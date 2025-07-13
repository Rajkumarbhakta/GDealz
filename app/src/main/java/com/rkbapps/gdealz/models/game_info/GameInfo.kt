package com.rkbapps.gdealz.models.game_info

import com.google.gson.annotations.SerializedName
import com.rkbapps.gdealz.models.deal.Assets


data class GameInfo(

    @SerializedName("id") val id: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("mature") val mature: Boolean? = null,
    @SerializedName("assets") val assets: Assets? = Assets(),
    @SerializedName("earlyAccess") val earlyAccess: Boolean? = null,
    @SerializedName("achievements") val achievements: Boolean? = null,
    @SerializedName("tradingCards") val tradingCards: Boolean? = null,
    @SerializedName("appid") val steamAppId: Int? = null,
    @SerializedName("tags") val tags: ArrayList<String> = arrayListOf(),
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("developers") val developers: ArrayList<Developers> = arrayListOf(),
    @SerializedName("publishers") val publishers: ArrayList<Developers> = arrayListOf(),
    @SerializedName("reviews") val reviews: ArrayList<Reviews> = arrayListOf(),
    @SerializedName("stats") val stats: Stats? = Stats(),
    @SerializedName("players") val players: Players? = Players(),
    @SerializedName("urls") val urls: Urls? = Urls()

)