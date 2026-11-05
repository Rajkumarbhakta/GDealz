package com.rkbapps.gdealz.ui.tab.deals

import android.content.Context
import androidx.compose.runtime.snapshotFlow
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.models.deal.DealItem
import com.rkbapps.gdealz.models.deal.Deals
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.FavStoreIds
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class DealsTabRepository @Inject constructor(
    private val isThereAnyDealApi: IsThereAnyDealApi,
    private val preferenceManager: PreferenceManager,
) {
    private val _deals = MutableStateFlow(UiState<List<Deals>>())
    val deals = _deals.asStateFlow()


    private val _isThereAnyDealFilter = MutableStateFlow(IsThereAnyDealFilters())
    val isThereAnyDealFilter = _isThereAnyDealFilter.asStateFlow()

    val currentCountry = preferenceManager.getStringPreference(PreferenceManager.SELECTED_COUNTRY)

    val favStoreList = preferenceManager.getObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds::class.java)

    private val isNsfwAllow = preferenceManager.getBooleanPreference(PreferenceManager.IS_NSFW_ALLOWED, false)

    val bigDropDeals: StateFlow<UiState<List<DealItem>>>
    field  = MutableStateFlow<UiState<List<DealItem>>>(UiState())


    @OptIn(ExperimentalCoroutinesApi::class)
    val getIsThereAnyDealPager =
        combine(isThereAnyDealFilter, currentCountry, isNsfwAllow) { filter, country, nsfw ->
            Triple(filter, country, nsfw)
        }.flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 20, maxSize = 100, initialLoadSize = 20),
                pagingSourceFactory = {
                    IsThereAnyDealPagingSource(
                        api = isThereAnyDealApi,
                        filter = it.first,
                        countryCode = it.second ?: "US",
                        isNsfw = it.third
                    )
                }
            ).flow
        }


    suspend fun getHighestPriceDropDeals(context: Context){
        bigDropDeals.value = UiState(isLoading = true)
        val filter = IsThereAnyDealFilters(sort = IsThereAnyDealSortingOptions.HIGHEST_PRICE_CUT)
        val countryCode = preferenceManager.getStringPreferenceSynchronous(PreferenceManager.SELECTED_COUNTRY)
        val isNsfw = preferenceManager.getBooleanPreferenceSynchronous(PreferenceManager.IS_NSFW_ALLOWED)

        val response = safeApiCall {
            isThereAnyDealApi.getDeals(
                country = countryCode ?: "US",
                offset = 0,
                limit = 20,
                sort = filter.sort.key,
                filter = filter.generateFilter()?:"",
                shops = filter.stores.joinToString(",") { it.toString() },
                mature = isNsfw ?:false
            )
        }

        when(response){
            is NetworkResponse.Error.HttpError -> {
                bigDropDeals.value =  UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }
            NetworkResponse.Error.NetworkError -> {
                bigDropDeals.value = UiState(error = context.getString(R.string.unable_to_connect))
            }
            NetworkResponse.Error.UnknownError -> {
                bigDropDeals.value = UiState(
                    error = context.getString(R.string.something_went_wrong_try_again)
                )
            }
            is NetworkResponse.Success<Deals> -> {
                bigDropDeals.value = UiState(data = response.value.list)
            }
        }
    }




    fun updateIsThereAnyDealFilter(filter: IsThereAnyDealFilters) {
        _isThereAnyDealFilter.value = filter
    }

    suspend fun updateCountry(value: String) = preferenceManager.saveStringPreference(PreferenceManager.SELECTED_COUNTRY,value)

}