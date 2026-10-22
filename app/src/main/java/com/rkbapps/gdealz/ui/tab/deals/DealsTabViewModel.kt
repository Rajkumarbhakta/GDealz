package com.rkbapps.gdealz.ui.tab.deals

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.rkbapps.gdealz.models.Filter
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.models.deal.Deal
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealsTabViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: DealsTabRepository,
) : ViewModel() {

    val deals = repository.deals
    val isThereAnyDealFilter = repository.isThereAnyDealFilter
    val bigDropDeals = repository.bigDropDeals

    val country = repository.currentCountry.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val favStoreIds = repository.favStoreList.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )


    init {
        viewModelScope.launch {
            repository.getHighestPriceDropDeals(context)
        }
    }

    val isThereAnyDeals = repository.getIsThereAnyDealPager.cachedIn(viewModelScope)

    fun updateIsThereAnyDealFilter(filter: IsThereAnyDealFilters) = repository.updateIsThereAnyDealFilter(filter)
    fun clearIsThereAnyDealFilter() = repository.updateIsThereAnyDealFilter(IsThereAnyDealFilters())
    fun updateCountry(value: String) = viewModelScope.launch {
        repository.updateCountry(value)
    }

}