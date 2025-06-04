package com.rkbapps.gdealz.ui.tab.free

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rkbapps.gdealz.models.Giveaway
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreeDealsViewModel @Inject constructor(
    private val repository: FreeDealsRepository,
    private val gson: Gson
) : ViewModel() {
    val giveaway = repository.giveaway

//    val currentSelectedOption = repository.currentSelectedOption

    init {
        viewModelScope.launch {
            repository.getFreeGames()
        }
    }

    fun getGiveaways(currentSelectedOption: Int) {
        viewModelScope.launch {
            repository.getFreeGamesByFilter(currentSelectedOption = currentSelectedOption)
        }
    }

//    fun changeCurrentPosition(position: Int){
//        repository.changeCurrentPosition(position)
//    }

    fun getGiveawayJson(giveaway: Giveaway): String {
        return gson.toJson(giveaway)
    }


}