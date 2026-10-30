package com.rkbapps.gdealz.ui.tab.free

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.FreeDealsFilter
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.util.toPlatformList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreeDealsViewModel @Inject constructor(
    private val repository: FreeDealsRepository,
    private val gson: Gson,
    private val giveawaysDao: GiveawaysDao
) : ViewModel() {

    private val _filter = MutableStateFlow(FreeDealsFilter())
    val filter = _filter.asStateFlow()


    val stores = giveawaysDao.getPlatforms().map { rows -> rows.flatMap { it.toPlatformList() }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val types = giveawaysDao.getTypes().map { rows -> rows.distinct().sorted() }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val giveaways = giveawaysDao.getGiveawaysByOrder()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val claimedGiveaway = giveawaysDao.getGiveawaysByClaimed(true).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val allUnclaimed = giveawaysDao.getGiveawaysByClaimed(false).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val unClaimedGiveaway = allUnclaimed.combine(_filter){ items, filter ->
        items.filter { filter.matches(it) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

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

    fun updateFilter(filter: FreeDealsFilter) {
        _filter.value = filter
    }

    fun clearFilter(){
        _filter.value = FreeDealsFilter()
    }

    /** How many unclaimed giveaways [filter] would show, for the filter sheet's apply button. */
    fun countUnclaimed(filter: FreeDealsFilter): Int = allUnclaimed.value.count { filter.matches(it) }

    fun setClaimed(giveaway: Giveaway, claimed: Boolean) {
        viewModelScope.launch {
            giveawaysDao.updateGiveaway(giveaway.copy(isClaimed = claimed))
        }
    }

}

private fun FreeDealsFilter.matches(giveaway: Giveaway): Boolean {
    val matchesStore = stores.isEmpty() || giveaway.platforms.toPlatformList().any { it in stores }
    val matchesType = types.isEmpty() || giveaway.type in types
    return matchesStore && matchesType
}
