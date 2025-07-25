package com.rkbapps.gdealz.ui.screens.steam_details.cheapshark

import com.google.gson.Gson
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.models.SteamGameData
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.SteamApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SteamDetailsRepository @Inject constructor(
    private val steamApi: SteamApi,
    private val gson: Gson,
    private val storeDao: StoreDao
) {

    private val _steamGameData = MutableStateFlow(UiState<SteamGameData>())
    val steamGameData = _steamGameData.asStateFlow()

    suspend fun getGameDetails(appId: String?){
        if (appId==null){
            _steamGameData.value = UiState(error = "Steam game Id not found")
            return
        }
        _steamGameData.value = UiState(isLoading = true)
        val response = safeApiCall { steamApi.getGameDetails(appId) }
        when(response){
            is NetworkResponse.Error.HttpError -> {
                _steamGameData.value = UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }
            NetworkResponse.Error.NetworkError -> {
                _steamGameData.value = UiState(error = "Unable to connect please check your internet connection.")
            }
            NetworkResponse.Error.UnknownError -> {
                _steamGameData.value = UiState(error = "Something went wrong! Try again later.")
            }
            is NetworkResponse.Success<Map<String, Any>> -> {
                try {
                    val data = response.value
                    val gameDetails = data[appId] as Map<String, Any>
                    val json = gson.toJson(gameDetails)
                    val gameData = gson.fromJson(json, SteamGameData::class.java)
                    _steamGameData.value = UiState(data = gameData)
                }catch (e: Exception){
                    _steamGameData.value = UiState(error = "Unable to get game details.")
                    e.printStackTrace()
                }

            }
        }
    }
}