package com.rkbapps.gdealz.ui.tab.search

import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.models.search.SearchResult
import com.rkbapps.gdealz.network.api.CheapSharkApi
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: CheapSharkApi,
    private val isThereAnyDealApi: IsThereAnyDealApi,
    @ApplicationContext private val context: Context
) {

    private val _searchResult = MutableStateFlow(UiState<List<Game>>())
    val searchResult = _searchResult.asStateFlow()


    private val _isThereAnyDealSearchResult = MutableStateFlow(UiState<List<SearchResult>>())
    val isThereAnyDealSearchResult =  _isThereAnyDealSearchResult.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val regex = "^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$".toRegex()



    suspend fun getSearchResult(){
        _isThereAnyDealSearchResult.emit(UiState(isLoading = true))

        if (regex.matches(searchQuery.value).not()) {
            _searchResult.emit(UiState(error = context.getString(R.string.invalid_search_query)))
            return
        }

        val response = safeApiCall { isThereAnyDealApi.getSearchResult(
            searchQuery = searchQuery.value,
            results = 100
        ) }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                _isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.http_error, response.errorCode)))
            }

            NetworkResponse.Error.NetworkError -> {
                _isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.network_error)))
            }

            NetworkResponse.Error.UnknownError -> {
                _isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.something_went_wrong)))
            }

            is NetworkResponse.Success<List<SearchResult>> -> {
                val data = response.value
                if (data.isNotEmpty()) {
                    _isThereAnyDealSearchResult.emit(UiState(data = data))
                } else {
                    _isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.no_results_found, searchQuery.value)))
                }
            }
        }
    }


    suspend fun getSearchResult(query: String) {
        _searchResult.emit(UiState(isLoading = true))

        if (regex.matches(query).not()) {
            _searchResult.emit(UiState(error = context.getString(R.string.invalid_search_query)))
            return
        }

        val response = safeApiCall { api.getGames(query) }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                _searchResult.emit(UiState(error = context.getString(R.string.http_error, response.errorCode)))
            }

            NetworkResponse.Error.NetworkError -> {
                _searchResult.emit(UiState(error = context.getString(R.string.network_error)))
            }

            NetworkResponse.Error.UnknownError -> {
                _searchResult.emit(UiState(error = context.getString(R.string.something_went_wrong)))
            }

            is NetworkResponse.Success<List<Game>> -> {
                val data = response.value
                if (data.isNotEmpty()) {
                    _searchResult.emit(UiState(data = data))
                } else {
                    _searchResult.emit(UiState(error = context.getString(R.string.no_results_found, query)))
                }
            }
        }
    }


    fun updateSearchQuery(value:String){
        _searchQuery.value = value
    }




}