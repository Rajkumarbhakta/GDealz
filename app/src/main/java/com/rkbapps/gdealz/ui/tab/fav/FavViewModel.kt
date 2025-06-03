package com.rkbapps.gdealz.ui.tab.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.db.entity.FavDeals
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavViewModel @Inject constructor(
    private val repository: FavRepository
): ViewModel() {

    val favList = repository.favList.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList(),
    )

    fun deleteAFav(favDeal: FavDeals){
        viewModelScope.launch {
            repository.deleteAFav(favDeal)
        }
    }

}