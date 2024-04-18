package com.rkbapps.gdealz.ui.tab.free.repository

import android.util.Log
import com.rkbapps.gdealz.api.GamePowerApi
import com.rkbapps.gdealz.api.GiveawayPlatforms
import com.rkbapps.gdealz.ui.tab.free.FreeGameItemsPosition
import com.rkbapps.gdealz.ui.tab.free.FreeGamesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FreeDealsRepository @Inject constructor(private val api: GamePowerApi) {
    private val _giveaways = MutableStateFlow<FreeGamesUiState>(FreeGamesUiState(isLoading = true))
    val giveaway: StateFlow<FreeGamesUiState> = _giveaways

//    val currentSelectedOption = mutableIntStateOf(FreeGameItemsPosition.PC)

    suspend fun getFreeGames(platform: String = GiveawayPlatforms.PC) {
        try {
            val response = api.getGiveawayByFilter(platform = platform)
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    _giveaways.emit(FreeGamesUiState(data = response.body()!!))
                }else{
                    _giveaways.emit(FreeGamesUiState(userMessage = "No active giveaways available at the moment, please try again later."))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _giveaways.emit(FreeGamesUiState(userMessage = "No active giveaways available at the moment, please try again later."))
        }
    }


    suspend fun getFreeGamesByFilter(currentSelectedOption: Int) {
        _giveaways.emit(FreeGamesUiState(isLoading = true))
        try {
            Log.d("CHECKING", "$currentSelectedOption")
            val response = api.getGiveawayByFilter(
                platform = when (currentSelectedOption) {
                    FreeGameItemsPosition.PC -> GiveawayPlatforms.PC
                    FreeGameItemsPosition.XBOX -> GiveawayPlatforms.XBOX
                    FreeGameItemsPosition.PS4 -> GiveawayPlatforms.PS4
                    FreeGameItemsPosition.ANDROID -> GiveawayPlatforms.ANDROID
                    FreeGameItemsPosition.IOS -> GiveawayPlatforms.IOS
                    else -> GiveawayPlatforms.PC
                },
            )
            if (response.isSuccessful) {
                Log.d("CHECKING", "${response.code()}")
                Log.d("CHECKING", "${response.raw()}")
                if (response.code() == 200) {
                    _giveaways.emit(FreeGamesUiState(data = response.body()!!))

                } else {
                    _giveaways.emit(FreeGamesUiState(userMessage = "No active giveaways available at the moment, please try again later."))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("CHECKING", "",e)
            _giveaways.emit(FreeGamesUiState(userMessage = "No active giveaways available at the moment, please try again later."))
        }
    }

//    fun changeCurrentPosition(position: Int) {
//        currentSelectedOption.value = position
//    }


}