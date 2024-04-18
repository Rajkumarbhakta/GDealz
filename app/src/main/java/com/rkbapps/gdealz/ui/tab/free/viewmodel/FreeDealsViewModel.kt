package com.rkbapps.gdealz.ui.tab.free.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.ui.tab.free.repository.FreeDealsRepository
import com.rkbapps.gdealz.ui.tab.free.FreeGamesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreeDealsViewModel @Inject constructor(private val repository: FreeDealsRepository):ViewModel() {
    val giveaway: StateFlow<FreeGamesUiState> = repository.giveaway

//    val currentSelectedOption = repository.currentSelectedOption

    init {
        viewModelScope.launch {
            repository.getFreeGames()
        }
    }

    fun getGiveaways(currentSelectedOption:Int){
        viewModelScope.launch {
            repository.getFreeGamesByFilter(currentSelectedOption = currentSelectedOption)
        }
    }

//    fun changeCurrentPosition(position: Int){
//        repository.changeCurrentPosition(position)
//    }


}