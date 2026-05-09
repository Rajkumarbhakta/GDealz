package com.rkbapps.gdealz.ui.tab.free

import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.network.api.GamePowerApi
import com.rkbapps.gdealz.network.GiveawayPlatforms
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FreeDealsRepository @Inject constructor(
    private val api: GamePowerApi,
    private val giveawaysDao: GiveawaysDao,
    @ApplicationContext private val context: Context
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
            _giveawaysState.value = UiState(data = data)
            return
        }
        when (val response = safeApiCall { api.getGiveawayByFilter(platform = platform) }) {
            is NetworkResponse.Error.HttpError -> {
                _giveawaysState.value =
                    UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }

            NetworkResponse.Error.NetworkError -> {
                _giveawaysState.value =
                    UiState(error = context.getString(R.string.unable_to_connect))
            }

            NetworkResponse.Error.UnknownError -> {
                _giveawaysState.value =
                    UiState(error = context.getString(R.string.no_active_giveaways))
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                if (data != null) {
                    saveToDatabase(data)
                    _giveawaysState.value = UiState(data = data)
                } else {
                    _giveawaysState.value =
                        UiState(error = context.getString(R.string.no_active_giveaways))
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
                    UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }

            NetworkResponse.Error.NetworkError -> {
                _giveawaysState.value =
                    UiState(error = context.getString(R.string.unable_to_connect))
            }

            NetworkResponse.Error.UnknownError -> {
                _giveawaysState.value =
                    UiState(error = context.getString(R.string.no_active_giveaways))
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                if (data != null) {
                    _giveawaysState.value = UiState(data = data)
                } else {
                    _giveawaysState.value =
                        UiState(error = context.getString(R.string.no_active_giveaways))
                }
            }
        }
    }


    suspend fun saveToDatabase(giveaways: List<Giveaway>) {
        giveawaysDao.replaceGiveaways(giveaways)
    }
}