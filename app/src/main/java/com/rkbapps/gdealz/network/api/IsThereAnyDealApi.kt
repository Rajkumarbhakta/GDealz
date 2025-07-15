package com.rkbapps.gdealz.network.api

import com.rkbapps.gdealz.models.deal.Deals
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.models.search.SearchResult
import com.rkbapps.gdealz.network.ApiConst
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IsThereAnyDealApi {

    @GET("/deals/v2")
    suspend fun getDeals(
        @Query("country") country: String = "US",
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
        @Query("filter", encoded = true) filter: String,
        @Query("nondeals") nonDeals: Boolean = false,
        @Query("mature") mature: Boolean = true,
        @Query("shops") shops: String,
        @Query("include_prices") includePrices: Boolean = true,
        @Query("key") apiKey: String = ApiConst.IS_THERE_ANY_DEAL_API_KEY
    ): Deals

    @GET("/games/info/v2")
    suspend fun getGameInfo(
        @Query("id") gameId: String,
        @Query("key") apiKey: String = ApiConst.IS_THERE_ANY_DEAL_API_KEY
    ): GameInfo

    @POST("/games/prices/v3")
    suspend fun getPrices(
        @Query("country") country:String = "US",
        @Query("deals") deals: Boolean = true,
        @Query("vouchers") vouchers: Boolean = true,
        @Query("key") apiKey: String = ApiConst.IS_THERE_ANY_DEAL_API_KEY,
        @Body gameIds: List<String> = emptyList()
    ): List<PriceDetail>

    @GET("/games/search/v1")
    suspend fun getSearchResult(
        @Query("title") searchQuery: String,
        @Query("results") results:Int = 20,
        @Query("key") apiKey: String = ApiConst.IS_THERE_ANY_DEAL_API_KEY,
    ): List<SearchResult>

}