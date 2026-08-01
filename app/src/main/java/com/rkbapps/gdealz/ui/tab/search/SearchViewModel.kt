package com.rkbapps.gdealz.ui.tab.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    val searchQuery = repository.searchQuery
    val isThereAnyDealSearchResult = repository.isThereAnyDealSearchResult

    fun search(){
        viewModelScope.launch {
            repository.getSearchResult()
        }
    }

    fun updateSearchQuery(query: String) {
        repository.updateSearchQuery(query)
    }

}