package com.rkbapps.gdealz.ui.tab.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.ui.tab.home.repository.HomeTabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(private val repository: HomeTabRepository) :
    ViewModel() {

        val deals :StateFlow<List<Deals>> = repository.deals

        init {
            viewModelScope.launch {
                repository.getAllDeals()
            }
        }


    fun getDealsByFilter(storeId:Int,upperPrice:Int){
        viewModelScope.launch {
            repository.getDealsByFilter(storeId, upperPrice)
        }
    }



}