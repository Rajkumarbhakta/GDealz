package com.rkbapps.gdealz.ui.screens.dealslookup.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rkbapps.gdealz.api.ApiConst.redirect
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.screens.dealslookup.repository.DealLookupRepository
import com.rkbapps.gdealz.ui.screens.dealslookup.repository.FavDealsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class DealLookupViewModel @Inject constructor(
    private val repository: DealLookupRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {

    val dealData = repository.dealsData
    val isFavDeal: State<Boolean> = repository.isFavDeal
    val dealFavStatus: StateFlow<FavDealsState> = repository.dealFavStatus
    val storeData: StateFlow<Store?> = repository.storeData

    val dealLookup = savedStateHandle.toRoute<Routes.DealsLookup>()

    val title = dealLookup.title

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dealLookup.dealId?.let {
                getDealsInfo(it)
                dealFavStatus(it)
            }
        }
    }


    fun dealFavStatus(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.isDealFav(id)
        }
    }

    fun toggleFavDeal(deal: DealsInfo){
        viewModelScope.launch(Dispatchers.IO){
            dealLookup.dealId?.let {
                repository.markFavDeals(deal, it)
            }
        }
    }

    suspend fun getDealsInfo(id:String){
        repository.getDealsInfo(id)
    }

    val redirectionUrl = dealLookup.dealId?.let { id -> redirect(id) }

}