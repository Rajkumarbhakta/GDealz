package com.rkbapps.gdealz.ui.tab.deals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rkbapps.gdealz.models.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealsTabViewModel @Inject constructor(private val repository: DealsTabRepository) :
    ViewModel() {

        val deals  = repository.deals

        val filter = repository.filter

        val stores = repository.storeFlow.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5_000), emptyList())


        init {
            viewModelScope.launch {

            }
        }

    fun getDealsByFilter(storeId:Int,upperPrice:Int){
        viewModelScope.launch {
            repository.getDealsByFilter(storeId, upperPrice)
        }
    }

    val dealsPagingData = repository.getDealsPager().cachedIn(viewModelScope)

    fun updateFilter(filter: Filter) = repository.updateFilter(filter)

}