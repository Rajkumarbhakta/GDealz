package com.rkbapps.gdealz.ui.screens.dealslookup.viewmodel

import androidx.lifecycle.ViewModel
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.ui.screens.dealslookup.repository.DealLookupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DealLookupViewModel @Inject constructor(private val repository: DealLookupRepository):ViewModel() {
    val gameData : StateFlow<DealsInfo> = repository.gameData
    suspend fun getDealsInfo(id:String){
        repository.getDealsInfo(id)
    }

}