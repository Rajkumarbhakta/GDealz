package com.rkbapps.gdealz.ui.tab.deals.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.api.NetworkResponse
import com.rkbapps.gdealz.api.safeApiCall
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.models.Filter
import com.rkbapps.gdealz.ui.tab.deals.DealsPagingSource
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class HomeTabRepository @Inject constructor(private val api:ApiInterface,
    private val storeDb: StoreDao
    ) {


    private val _deals = MutableStateFlow(UiState<List<Deals>>())
    val deals = _deals.asStateFlow()

    private val _filter = MutableStateFlow<Filter>(Filter())
    val filter = _filter.asStateFlow()


    suspend fun getAllDeals() {
        _deals.value = UiState(isLoading = true)
        val response = safeApiCall { api.getAllDeals() }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                _deals.value =
                    UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                _deals.value =
                    UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                _deals.value = UiState(error = "Something went wrong")
            }

            is NetworkResponse.Success<List<Deals>> -> {
                _deals.value = UiState(data = response.value)
            }
        }
    }

    suspend fun getDealsByFilter(storeId: Int, upperPrice: Int) {
        _deals.value = UiState(isLoading = true)
        val response = safeApiCall { api.getDeals(storeId, upperPrice) }
        when (response) {
            is NetworkResponse.Error.HttpError -> {
                _deals.value =
                    UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                _deals.value =
                    UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                _deals.value = UiState(error = "Something went wrong")
            }

            is NetworkResponse.Success<List<Deals>> -> {
                _deals.value = UiState(data = response.value)
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDealsPager() = _filter
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 20, maxSize = 100, initialLoadSize = 20),
                pagingSourceFactory = { DealsPagingSource(api,it)}
            ).flow
        }


    fun updateFilter(filter: Filter){
        _filter.value = filter
    }

    val storeFlow = storeDb.findAll()





}