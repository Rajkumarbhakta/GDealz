package com.rkbapps.gdealz.ui.tab.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository): ViewModel() {
    val searchResult: StateFlow<SearchUiState> = repository.searchResult

    init {
        viewModelScope.launch {
            repository.getSearchResult("batman")
        }
    }

    fun search(query:String){
        viewModelScope.launch {
            repository.getSearchResult(query)
        }
    }

}