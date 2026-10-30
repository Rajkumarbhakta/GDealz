package com.rkbapps.gdealz.ui.screens.game_info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.gdealz.models.deal.Deal
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameInfoViewModel @Inject constructor(
    private val repository: GameInfoRepository,
    saveStateHandle: SavedStateHandle
) : ViewModel() {

    val gameInfo = repository.gameInfo
    val gamePriceInfo =  repository.gamePriceInfo
    val priceHistory = repository.priceHistory
    val isFavDeal = repository.isFavDeal
    val dealFavStatus = repository.dealFavStatus

    private val dealLookup = saveStateHandle.toRoute<Routes.GameInfo>()
    val title = dealLookup.title
    val gameId = dealLookup.gameId

    init {
        viewModelScope.launch {
            repository.getGameInfo(dealLookup.gameId)
            launch { repository.getPriceHistory(dealLookup.gameId) }
            repository.getGamePriceInfo(dealLookup.gameId, dealsOnly = false)
            dealFavStatus(dealLookup.gameId)
        }
    }

    fun dealFavStatus(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.isDealFav(id)
        }
    }

    fun toggleFavDeal(gameInfo: GameInfo,prices: List<Deals>){
        viewModelScope.launch(Dispatchers.IO){
            repository.markFavDeals(gameInfo = gameInfo, prices = prices)
        }
    }

}