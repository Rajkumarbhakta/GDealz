package com.rkbapps.gdealz.ui.tab.free

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreeDealsViewModel @Inject constructor(
    private val repository: FreeDealsRepository,
    private val gson: Gson,
    private val giveawaysDao: GiveawaysDao
) : ViewModel() {

    val giveaways = giveawaysDao.getGiveawaysByOrder().stateIn(viewModelScope, SharingStarted.Lazily, emptyList(),)

    val giveawayState = repository.giveawayState


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

    fun getGiveawayJson(giveaway: Giveaway): String {
        return gson.toJson(giveaway)
    }


}