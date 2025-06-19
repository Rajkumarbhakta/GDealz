package com.rkbapps.gdealz.network

import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApi {
    @GET("appdetails")
    suspend fun getGameDetails(@Query("appids") appId: String): Map<String, Any>

}