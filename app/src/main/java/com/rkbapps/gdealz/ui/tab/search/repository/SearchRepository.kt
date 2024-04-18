package com.rkbapps.gdealz.ui.tab.search.repository

import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.ui.tab.search.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api:ApiInterface) {

    private val _searchResult = MutableStateFlow(SearchUiState(isLoading = true))

    val searchResult:StateFlow<SearchUiState> = _searchResult


    suspend fun getSearchResult(query:String){
        _searchResult.emit(SearchUiState(isLoading = true))
        try {
            val response = api.getGames(query)
            if (response.isSuccessful){
                _searchResult.emit(SearchUiState(data = response.body()!!))
            }else{
                _searchResult.emit(SearchUiState(userMessage ="Something went wrong."))
            }
        }catch (e:Exception){
            _searchResult.emit(SearchUiState(userMessage = "Something went wrong."))
        }
    }




}