package com.rkbapps.gdealz.ui.tab.free.repository

import android.util.Log
import com.rkbapps.gdealz.api.GamePowerApi
import com.rkbapps.gdealz.api.GiveawayPlatforms
import com.rkbapps.gdealz.api.NetworkResponse
import com.rkbapps.gdealz.api.safeApiCall
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.ui.tab.free.FreeGameItemsPosition
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FreeDealsRepository @Inject constructor(private val api: GamePowerApi) {
    private val _giveaways = MutableStateFlow(UiState<List<Giveaway>>())
    val giveaway = _giveaways.asStateFlow()


    suspend fun getFreeGames(platform: String = GiveawayPlatforms.PC) {
        _giveaways.value = UiState(isLoading = true)
        when(val response = safeApiCall { api.getGiveawayByFilter(platform = platform) }){
            is NetworkResponse.Error.HttpError -> {
                _giveaways.value = UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }
            NetworkResponse.Error.NetworkError -> {
                _giveaways.value = UiState(error = "Unable to connect please check your internet connection.")
            }
            NetworkResponse.Error.UnknownError -> {
                _giveaways.value = UiState(error = "Something went wrong")
            }
            is NetworkResponse.Success<List<Giveaway>> -> {
                _giveaways.value = UiState(data = response.value)
            }
        }
    }

    suspend fun getFreeGamesByFilter(currentSelectedOption: Int) {
        _giveaways.value = UiState(isLoading = true)

        val platform = when (currentSelectedOption) {
            FreeGameItemsPosition.PC -> GiveawayPlatforms.PC
            FreeGameItemsPosition.XBOX -> GiveawayPlatforms.XBOX
            FreeGameItemsPosition.PS4 -> GiveawayPlatforms.PS4
            FreeGameItemsPosition.ANDROID -> GiveawayPlatforms.ANDROID
            FreeGameItemsPosition.IOS -> GiveawayPlatforms.IOS
            else -> GiveawayPlatforms.PC
        }

        when(val response = safeApiCall { api.getGiveawayByFilter(platform = platform) }){
            is NetworkResponse.Error.HttpError -> {
                _giveaways.value = UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }
            NetworkResponse.Error.NetworkError -> {
                _giveaways.value = UiState(error = "Unable to connect please check your internet connection.")
            }
            NetworkResponse.Error.UnknownError -> {
                _giveaways.value = UiState(error = "Something went wrong")
            }
            is NetworkResponse.Success<List<Giveaway>> -> {
                _giveaways.value = UiState(data = response.value)
            }
        }

    }
}