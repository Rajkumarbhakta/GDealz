package com.rkbapps.gdealz.ui.screens.steam_details.cheapshark

import com.google.gson.Gson
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.models.SteamGameData
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.SteamApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SteamDetailsRepository @Inject constructor(
    private val steamApi: SteamApi,
    private val gson: Gson,
    private val storeDao: StoreDao,
    @ApplicationContext private val context: Context
) {

    private val _steamGameData = MutableStateFlow(UiState<SteamGameData>())
    val steamGameData = _steamGameData.asStateFlow()

    suspend fun getGameDetails(appId: String?){
        if (appId==null){
            _steamGameData.value = UiState(error = context.getString(R.string.steam_game_id_not_found))
            return
        }
        _steamGameData.value = UiState(isLoading = true)
        val response = safeApiCall { steamApi.getGameDetails(appId) }
        when(response){
            is NetworkResponse.Error.HttpError -> {
                _steamGameData.value = UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }
            NetworkResponse.Error.NetworkError -> {
                _steamGameData.value = UiState(error = context.getString(R.string.unable_to_connect))
            }
            NetworkResponse.Error.UnknownError -> {
                _steamGameData.value = UiState(error = context.getString(R.string.something_went_wrong_try_again))
            }
            is NetworkResponse.Success<Map<String, Any>> -> {
                try {
                    val data = response.value
                    val gameDetails = data[appId] as Map<String, Any>
                    val json = gson.toJson(gameDetails)
                    val gameData = gson.fromJson(json, SteamGameData::class.java)
                    _steamGameData.value = UiState(data = gameData)
                }catch (e: Exception){
                    _steamGameData.value = UiState(error = context.getString(R.string.unable_to_get_game_details))
                    e.printStackTrace()
                }

            }
        }
    }
}