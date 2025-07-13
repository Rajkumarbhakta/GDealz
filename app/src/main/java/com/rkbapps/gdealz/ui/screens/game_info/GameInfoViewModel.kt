package com.rkbapps.gdealz.ui.screens.game_info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameInfoViewModel @Inject constructor(
    private val repository: GameInfoRepository,
    private val saveStateHandle: SavedStateHandle
) : ViewModel() {

    val gameInfo = repository.gameInfo
    val isFavDeal = repository.isFavDeal
    val dealFavStatus = repository.dealFavStatus

    private val dealLookup = saveStateHandle.toRoute<Routes.GameInfo>()
    val title = dealLookup.title

    init {
        viewModelScope.launch {
            repository.getGameInfo(dealLookup.gameId)
            dealFavStatus(dealLookup.gameId)
        }
    }

    fun dealFavStatus(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.isDealFav(id)
        }
    }

    fun toggleFavDeal(deal: GameInfo){
        viewModelScope.launch(Dispatchers.IO){
            dealLookup.gameId.let {
                repository.markFavDeals(deal, it)
            }
        }
    }







}