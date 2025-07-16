package com.rkbapps.gdealz.ui.tab.deals

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.models.deal.DealItem
import com.rkbapps.gdealz.models.deal.Deals
import com.rkbapps.gdealz.network.ApiConst
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import java.io.IOException


private const val PAGE_SIZE = 20

class IsThereAnyDealPagingSource(
    private val api: IsThereAnyDealApi,
    private val filter: IsThereAnyDealFilters,
    private val countryCode:String = ApiConst.COUNTRY,
    private val isNsfw: Boolean = false
) : PagingSource<Int, DealItem>() {
    override fun getRefreshKey(state: PagingState<Int, DealItem>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.let { page ->
                page.prevKey?.plus(PAGE_SIZE) ?: page.nextKey?.minus(PAGE_SIZE)
            }
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DealItem> {
        try {
            val position = params.key ?: 0

            val filterString = filter.generateFilter() ?: ""
            Log.d("Filter", "generateDealFilter: $filterString")

            val response = safeApiCall {
                api.getDeals(
                    country = countryCode,
                    offset = position,
                    limit = 20,
                    sort = filter.sort.key,
                    filter = filterString,
                    shops = filter.stores.joinToString(",") { it.toString() },
                    mature = isNsfw
                )
            }
            val result = when (response) {
                is NetworkResponse.Error.HttpError -> {
                    if (response.errorCode>399 && response.errorCode<=499){
                        LoadResult.Error(Exception("${response.errorCode}:No game found"))
                    }else{
                        LoadResult.Error(response.error)
                    }
                }
                NetworkResponse.Error.NetworkError -> LoadResult.Error(IOException("Network"))
                NetworkResponse.Error.UnknownError -> LoadResult.Error(Exception("Unknown"))
                is NetworkResponse.Success<Deals> -> {
                    Log.d("PagingSource", "load: ${response.value.list}")
                    val data = response.value.list.filter { item -> item.type != "dlc" }
                    val nextKey =
                        if (data.isNotEmpty() && response.value.hasMore == true) position + PAGE_SIZE
                        else null
                    val prevKey = if (position == 0) null else position - PAGE_SIZE
                    LoadResult.Page(data, prevKey, nextKey)
                }
            }
            Log.d("PagingSource", "load: $result")
            return result
        } catch (e: Exception) {
            Log.d("PagingSource", "load: ${e.message}")
            return LoadResult.Error(e)
        }
    }
}