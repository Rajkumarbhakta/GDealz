package com.rkbapps.gdealz.ui.tab.deals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.rkbapps.gdealz.models.Filter
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.models.deal.Deal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealsTabViewModel @Inject constructor(
    private val repository: DealsTabRepository,
    private val gson: Gson
) : ViewModel() {

    val deals = repository.deals
    val filter = repository.filter
    val isThereAnyDealFilter = repository.isThereAnyDealFilter


//    val stores = repository.storeFlow.stateIn(
//        viewModelScope,
//        SharingStarted.Lazily,
//        emptyList()
//    )


    init {
        viewModelScope.launch {

        }
    }

    fun getDealsByFilter(storeId:Int,upperPrice:Int){
        viewModelScope.launch {
            repository.getDealsByFilter(storeId, upperPrice)
        }
    }

//    val dealsPagingData = repository.getDealsPager().cachedIn(viewModelScope)

    val isThereAnyDeals = repository.getIsThereAnyDealPager.cachedIn(viewModelScope)

    fun dealToJson(deal: Deal): String{
        return gson.toJson(deal)
    }


    fun updateFilter(filter: Filter) = repository.updateFilter(filter)

    fun updateIsThereAnyDealFilter(filter: IsThereAnyDealFilters) = repository.updateIsThereAnyDealFilter(filter)
    fun clearIsThereAnyDealFilter() = repository.updateIsThereAnyDealFilter(IsThereAnyDealFilters())

}