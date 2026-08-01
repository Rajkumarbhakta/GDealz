package com.rkbapps.gdealz.ui.tab.deals

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.util.FavStoreIds
import com.rkbapps.gdealz.util.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun updateIsThereAnyDealFilter(filter: IsThereAnyDealFilters) {
        _isThereAnyDealFilter.value = filter
    }

    suspend fun updateCountry(value: String) = preferenceManager.saveStringPreference(PreferenceManager.SELECTED_COUNTRY,value)

}