package com.rkbapps.gdealz.api

import com.rkbapps.gdealz.models.Giveaway
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GamePowerApi {

    @GET("giveaways")
    suspend fun getAllGiveaways():Response<List<Giveaway>>

    @GET("giveaways")
    suspend fun getGiveawaysByPlatform(@Query("platform") platform:String):Response<List<Giveaway>>

    @GET("filter")
    suspend fun getGiveawayByFilter(
        @Query("platform",encoded = true) platform:String = GiveawayPlatforms.PC,
        @Query("type") type:String = "game",
        @Query("sort-by") sortBY :String = GiveawaySortingOptions.DATE,
    ):List<Giveaway>

}