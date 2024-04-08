package com.rkbapps.gdealz.api

import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.models.GameLookUp
import com.rkbapps.gdealz.models.Store
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("deals")
    suspend fun getDeals(@Query("storeID") storeId:Int,@Query("upperPrice")upperPrice:Int):Response<List<Deals>>

    @GET("deals")
    suspend fun getAllDeals():Response<List<Deals>>

    @GET("deals")
    suspend fun getDealsInfo(@Query("id")id:String):Response<DealsInfo>

    @GET("games?title=batman")
    suspend fun getGames(@Query("title")title:String):Response<List<Game>>

    @GET("games")
    suspend fun getGameInfo(@Query("id")id:Int):Response<GameLookUp>

    @GET("stores")
    suspend fun getAllStore():Response<List<Store>>


}