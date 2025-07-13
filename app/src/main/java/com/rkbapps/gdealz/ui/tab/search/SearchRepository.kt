package com.rkbapps.gdealz.ui.tab.search

import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.network.api.CheapSharkApi
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api: CheapSharkApi) {

    private val _searchResult = MutableStateFlow(UiState<List<Game>>())

    val searchResult = _searchResult.asStateFlow()

    private val regex = "^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$".toRegex()


    suspend fun getSearchResult(query: String) {
        _searchResult.emit(UiState(isLoading = true))

        if (regex.matches(query).not()) {
            _searchResult.emit(UiState(error = "Invalid search query. Please use alphanumeric characters and spaces only."))
            return
        }

        val response = safeApiCall { api.getGames(query) }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                _searchResult.emit(UiState(error = "HTTP Error: ${response.errorCode}. Please try again later."))
            }

            NetworkResponse.Error.NetworkError -> {
                _searchResult.emit(UiState(error = "Network Error. Please check your connection."))
            }

            NetworkResponse.Error.UnknownError -> {
                _searchResult.emit(UiState(error = "Unknown Error. Please try again later."))
            }

            is NetworkResponse.Success<List<Game>> -> {
                val data = response.value
                if (data.isNotEmpty()) {
                    _searchResult.emit(UiState(data = data))
                } else {
                    _searchResult.emit(UiState(error = "No results found for '$query'."))
                }
            }
        }
    }




}