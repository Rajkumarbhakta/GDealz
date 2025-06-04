package com.rkbapps.gdealz.network

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
    suspend fun getDeals(
        @Query("storeID") storeId: Int,
        @Query("upperPrice") upperPrice: Int
    ): List<Deals>

    @GET("deals")
    suspend fun getAllDeals(
        @Query("storeID") storeId: Int? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("sortBy") shortBy: String = ShortingOptions.Rating.option,
        @Query("desc") orderBy: Boolean = false,
        @Query("lowerPrice") lowerPrice: Int = 0,
        @Query("upperPrice") upperPrice: Int = 50
    ): List<Deals>

    @GET("deals")
    suspend fun getDealsInfo(@Query("id",encoded = true) id: String): DealsInfo


    @GET("games")
    suspend fun getGames(@Query("title") title: String): Response<List<Game>>

    @GET("games")
    suspend fun getGameInfo(@Query("id") id: Int): Response<GameLookUp>

    @GET("stores")
    suspend fun getAllStore(): Response<List<Store>>


}