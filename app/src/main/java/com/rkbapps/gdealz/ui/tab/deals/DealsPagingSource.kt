package com.rkbapps.gdealz.ui.tab.deals

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.api.NetworkResponse
import com.rkbapps.gdealz.api.safeApiCall
import com.rkbapps.gdealz.models.Deals
import retrofit2.HttpException

class DealsPagingSource(
    private val api: ApiInterface
):PagingSource<Int, Deals>() {
    override fun getRefreshKey(state: PagingState<Int, Deals>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Deals> {
        try {
            val position = params.key ?: 0
            val response = safeApiCall { api.getAllDeals(
                pageNumber = position
            ) }
            return when(response){
                is NetworkResponse.Error.HttpError -> {
                    LoadResult.Error(response.error)
                }
                NetworkResponse.Error.NetworkError -> {
                    LoadResult.Invalid()
                }
                NetworkResponse.Error.UnknownError ->{
                    LoadResult.Invalid()
                }
                is NetworkResponse.Success<List<Deals>> -> {
                    LoadResult.Page(
                        data = response.value,
                        prevKey = if (position == 0) null else (position - 1),
                        nextKey = if (position == 5) null else position + 1,
                    )
                }
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}