package com.rkbapps.gdealz.ui.tab.search

import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.models.search.SearchResult
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val isThereAnyDealApi: IsThereAnyDealApi,
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context
) {
    val isThereAnyDealSearchResult:StateFlow<UiState<List<SearchResult>>>
    field = MutableStateFlow(UiState<List<SearchResult>>())

    val searchQuery:StateFlow<String>
    field = MutableStateFlow("")

    /** Cheapest current offer per game id. Games with no offer in the user's country are absent. */
    val searchPrices:StateFlow<UiState<Map<String, Deals>>>
    field = MutableStateFlow(UiState<Map<String, Deals>>())

    // Valid queries are letters, digits and spaces, so a newline is a safe separator.
    val recentSearches = preferenceManager.getStringPreference(PreferenceManager.RECENT_SEARCHES)
        .map { it?.split('\n')?.filter(String::isNotBlank).orEmpty() }

    private val regex = "^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$".toRegex()


    suspend fun getSearchResult(){
        isThereAnyDealSearchResult.emit(UiState(isLoading = true))

        if (regex.matches(searchQuery.value).not()) {
            isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.invalid_search_query)))
            return
        }

        val response = safeApiCall { isThereAnyDealApi.getSearchResult(
            searchQuery = searchQuery.value,
            results = 100
        ) }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.http_error, response.errorCode)))
            }

            NetworkResponse.Error.NetworkError -> {
                isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.network_error)))
            }

            NetworkResponse.Error.UnknownError -> {
                isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.something_went_wrong)))
            }

            is NetworkResponse.Success<List<SearchResult>> -> {
                val data = response.value
                if (data.isNotEmpty()) {
                    isThereAnyDealSearchResult.emit(UiState(data = data))
                } else {
                    isThereAnyDealSearchResult.emit(UiState(error = context.getString(R.string.no_results_found, searchQuery.value)))
                }
            }
        }
    }


    fun updateSearchQuery(value:String){
        searchQuery.value = value
    }

    /** Search only returns titles and art, so prices for the current results come from a second call. */
    suspend fun getSearchPrices() {
        val gameIds = isThereAnyDealSearchResult.value.data?.map { it.id }.orEmpty()
        if (gameIds.isEmpty()) {
            searchPrices.value = UiState()
            return
        }
        searchPrices.value = UiState(isLoading = true)

        val country = preferenceManager.getStringPreferenceSynchronous(PreferenceManager.SELECTED_COUNTRY, "US") ?: "US"

        // deals = false keeps full-price offers, so games that aren't on sale still show a price.
        when (val response = safeApiCall { isThereAnyDealApi.getPrices(country = country, deals = false, gameIds = gameIds) }) {
            is NetworkResponse.Error -> {
                searchPrices.value = UiState(error = context.getString(R.string.something_went_wrong))
            }
            is NetworkResponse.Success<List<PriceDetail>> -> {
                val prices = response.value.mapNotNull { detail ->
                    val id = detail.id ?: return@mapNotNull null
                    val cheapest = detail.deals
                        .filter { it.price?.amount != null }
                        .minByOrNull { it.price?.amount ?: Double.MAX_VALUE }
                        ?: return@mapNotNull null
                    id to cheapest
                }.toMap()
                searchPrices.value = UiState(data = prices)
            }
        }
    }

    fun clearSearch() {
        searchQuery.value = ""
        isThereAnyDealSearchResult.value = UiState()
        searchPrices.value = UiState()
    }

    suspend fun addRecentSearch(query: String) {
        val recent = (listOf(query) + recentSearches.first().filterNot { it.equals(query, ignoreCase = true) })
            .take(MAX_RECENT_SEARCHES)
        preferenceManager.saveStringPreference(PreferenceManager.RECENT_SEARCHES, recent.joinToString("\n"))
    }

    suspend fun clearRecentSearches() = preferenceManager.clearPreference(PreferenceManager.RECENT_SEARCHES)

    private companion object {
        const val MAX_RECENT_SEARCHES = 8
    }




}