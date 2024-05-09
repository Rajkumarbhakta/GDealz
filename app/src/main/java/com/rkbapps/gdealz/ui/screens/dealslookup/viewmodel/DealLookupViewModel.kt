package com.rkbapps.gdealz.ui.screens.dealslookup.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.ui.screens.dealslookup.repository.DealLookupRepository
import com.rkbapps.gdealz.ui.screens.dealslookup.repository.FavDealsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DealLookupViewModel @Inject constructor(private val repository: DealLookupRepository):ViewModel() {

    val gameData : StateFlow<DealsInfo> = repository.gameData
    val isFavDeal: State<Boolean> = repository.isFavDeal
    val dealFavStatus: StateFlow<FavDealsState> = repository.dealFavStatus
    val storeData: StateFlow<Store?> = repository.storeData

    fun dealFavStatus(id:String){
        repository.isDealFav(id)
    }

    fun toggleFavDeal(deal:DealsInfo,dealId:String){
        repository.markFavDeals(deal, dealId)
    }

    suspend fun getDealsInfo(id:String){
        repository.getDealsInfo(id)
    }

}