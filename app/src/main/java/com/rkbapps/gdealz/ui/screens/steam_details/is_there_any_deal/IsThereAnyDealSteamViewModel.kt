package com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal

import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.screens.dealslookup.FavDealsState
import com.rkbapps.gdealz.ui.screens.game_info.GameInfoRepository
import com.rkbapps.gdealz.ui.screens.steam_details.cheapshark.SteamDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IsThereAnyDealSteamViewModel @Inject constructor(
    private val steamDetailsRepository: SteamDetailsRepository,
    private val gameInfoRepository: GameInfoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val steamGameData = steamDetailsRepository.steamGameData
    val gamePriceInfo = gameInfoRepository.gamePriceInfo
    val gameInfo = gameInfoRepository.gameInfo

    val isFavDeal: State<Boolean> = gameInfoRepository.isFavDeal
    val dealFavStatus: StateFlow<FavDealsState> = gameInfoRepository.dealFavStatus

    private val route = savedStateHandle.toRoute<Routes.IsThereAnyDealSteamGameDetails>()
    val gameId = route.gameId
    val title = route.title

    init {
        viewModelScope.launch {
            dealFavStatus(route.gameId)
            this.async { gameInfoRepository.getGameInfo(gameId = route.gameId) }.await()
            steamDetailsRepository.getGameDetails(appId = gameInfo.value.data?.steamAppId?.toString())
            gameInfo.value.data?.steamAppId?.let {
                gameInfoRepository.getGamePriceInfo(route.gameId)
            }

        }
    }

    fun dealFavStatus(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            gameInfoRepository.isDealFav(id)
        }
    }

    fun toggleFavDeal(gameInfo: GameInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            route.gameId.let {
                gameInfoRepository.markFavDeals(gameInfo)
            }
        }
    }






}