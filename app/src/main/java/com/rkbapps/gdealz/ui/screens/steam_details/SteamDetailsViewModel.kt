package com.rkbapps.gdealz.ui.screens.steam_details

import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.network.ApiConst
import com.rkbapps.gdealz.ui.screens.dealslookup.DealLookupRepository
import com.rkbapps.gdealz.ui.screens.dealslookup.FavDealsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SteamDetailsViewModel @Inject constructor(
    private val repository: SteamDetailsRepository,
    private val dealsLookupRepository: DealLookupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dealData = dealsLookupRepository.dealsData
    val isFavDeal: State<Boolean> = dealsLookupRepository.isFavDeal
    val dealFavStatus: StateFlow<FavDealsState> = dealsLookupRepository.dealFavStatus
    val storeData: StateFlow<Store?> = dealsLookupRepository.storeData

    val steamGameData = repository.steamGameData

    val dealLookup = savedStateHandle.toRoute<Routes.SteamGameDetails>()


    val title = dealLookup.title


    init {
        viewModelScope.launch(Dispatchers.IO) {
            getGameData(dealLookup.steamId)
            dealLookup.dealId?.let {
                getDealsInfo(it)
                dealFavStatus(it)
            }
        }
    }

    fun getGameData(appId: String) {
        viewModelScope.launch {
            repository.getGameDetails(appId)
        }
    }


    fun dealFavStatus(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dealsLookupRepository.isDealFav(id)
        }
    }

    fun toggleFavDeal(deal: DealsInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            dealLookup.dealId?.let {
                dealsLookupRepository.markFavDeals(deal, it)
            }
        }
    }

    suspend fun getDealsInfo(id: String) {
        dealsLookupRepository.getDealsInfo(id)
    }


    val redirectionUrl = dealLookup.dealId?.let { id -> ApiConst.redirect(id) }
}