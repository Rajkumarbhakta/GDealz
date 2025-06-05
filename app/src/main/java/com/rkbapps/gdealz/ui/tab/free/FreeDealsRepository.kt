package com.rkbapps.gdealz.ui.tab.free

import android.util.Log
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.network.GamePowerApi
import com.rkbapps.gdealz.network.GiveawayPlatforms
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FreeDealsRepository @Inject constructor(
    private val api: GamePowerApi,
    private val giveawaysDao: GiveawaysDao
) {

    private val _giveawaysState = MutableStateFlow(UiState<List<Giveaway>>())
    val giveawayState = _giveawaysState.asStateFlow()


    suspend fun getFreeGames(platform: String = GiveawayPlatforms.PC) {
        _giveawaysState.value = UiState(isLoading = true)
        val data = try {
            giveawaysDao.getAllGiveaways()
        } catch (e: Exception) {
            emptyList()
        }
        if (data.isNotEmpty()) {
            _giveawaysState.value = UiState(data=data)
            return
        }
        when (val response = safeApiCall { api.getGiveawayByFilter(platform = platform) }) {
            is NetworkResponse.Error.HttpError -> {
                _giveawaysState.value =
                    UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                _giveawaysState.value =
                    UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                _giveawaysState.value =
                    UiState(error = "No active giveaways available at the moment, please try again later.")
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                Log.d("FreeDealsRepository", "Data from API: ${data?.size}")
                if (data != null) {
                    val dataToSave = data.subList(3, data.size) // Skip first 3 items
                    Log.d("FreeDealsRepository", "Data from API: ${dataToSave?.size}")
                    saveToDatabase(dataToSave)
                    _giveawaysState.value = UiState(data = data)

                } else {
                    _giveawaysState.value =
                        UiState(error = "No active giveaways available at the moment, please try again later.")
                }
            }
        }
    }

    suspend fun getFreeGamesByFilter(currentSelectedOption: Int) {
        _giveawaysState.value = UiState(isLoading = true)

        val platform = when (currentSelectedOption) {
            FreeGameItemsPosition.PC -> GiveawayPlatforms.PC
            FreeGameItemsPosition.XBOX -> GiveawayPlatforms.XBOX
            FreeGameItemsPosition.PS4 -> GiveawayPlatforms.PS4
            FreeGameItemsPosition.ANDROID -> GiveawayPlatforms.ANDROID
            FreeGameItemsPosition.IOS -> GiveawayPlatforms.IOS
            else -> GiveawayPlatforms.PC
        }

        when (val response = safeApiCall { api.getGiveawayByFilter(platform = platform) }) {
            is NetworkResponse.Error.HttpError -> {
                _giveawaysState.value =
                    UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                _giveawaysState.value =
                    UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                _giveawaysState.value =
                    UiState(error = "No active giveaways available at the moment, please try again later.")
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                if (data != null) {
                    _giveawaysState.value = UiState(data = data)
                } else {
                    _giveawaysState.value =
                        UiState(error = "No active giveaways available at the moment, please try again later.")
                }
            }
        }

    }


    suspend fun saveToDatabase(giveaways: List<Giveaway>) {
        giveawaysDao.replaceGiveaways(giveaways)
    }
}