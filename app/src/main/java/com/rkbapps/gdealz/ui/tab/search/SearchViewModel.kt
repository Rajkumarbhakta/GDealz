package com.rkbapps.gdealz.ui.tab.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    val searchQuery = repository.searchQuery
    val isThereAnyDealSearchResult = repository.isThereAnyDealSearchResult
    val searchPrices = repository.searchPrices

    val recentSearches = repository.recentSearches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var searchJob: Job? = null

    fun search(){
        viewModelScope.launch {
            repository.getSearchResult()
        }
    }

    fun updateSearchQuery(query: String) {
        repository.updateSearchQuery(query)
    }

    /** Runs [query], then fills in prices. A newer search replaces one still in flight. */
    fun searchWithPrices(query: String = searchQuery.value) {
        // Keyboards often leave a trailing space, which the query check would reject.
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        repository.updateSearchQuery(trimmed)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            repository.getSearchResult()
            repository.getSearchPrices()
            if (isThereAnyDealSearchResult.value.data != null) repository.addRecentSearch(trimmed)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        repository.clearSearch()
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

}