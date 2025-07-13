package com.rkbapps.gdealz.ui.screens.dealslookup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rkbapps.gdealz.network.api.CheapSharkApi
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class DealLookupRepository @Inject constructor(
    private val api: CheapSharkApi,
    private val storeDao: StoreDao,
    private val favDealsDao: FavDealsDao
) {
    private val _dealsData = MutableStateFlow(UiState<DealsInfo>())
    val dealsData = _dealsData.asStateFlow()

    private val isFav = mutableStateOf(false)
    val isFavDeal: State<Boolean> = isFav

    private val _dealFavStatus = MutableStateFlow<FavDealsState>(FavDealsState())
    val dealFavStatus: StateFlow<FavDealsState> = _dealFavStatus

    private val _storeData = MutableStateFlow<Store?>(null)
    val storeData: StateFlow<Store?> = _storeData


    suspend fun getDealsInfo(id: String) {
        _dealsData.value = UiState(isLoading = true)
        when (val response = safeApiCall { api.getDealsInfo(id) }) {
            is NetworkResponse.Error.HttpError -> {
                _dealsData.value =
                    UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                _dealsData.value =
                    UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                _dealsData.value = UiState(error = "Something went wrong")
            }

            is NetworkResponse.Success<DealsInfo> -> {
                val data = response.value
                _dealsData.value = UiState(data = data)
                data.gameInfo?.storeID?.let {
                    getStoreInfo(it)
                }

            }
        }
    }


    private suspend fun getStoreInfo(id: String) {
        try {
            val store = storeDao.findById(id)
            _storeData.emit(store)
        } catch (e: Exception) {
            _storeData.emit(null)
            e.printStackTrace()
        }
    }

    suspend fun markFavDeals(deal: DealsInfo, dealId: String) {
        try {
            if (isFavDeal.value) {
                removeFromFav(dealId)
            } else {
                markDealsInFav(deal, dealId)
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    suspend fun isDealFav(dealId: String) {

        try {
            val isExist = favDealsDao.isExistsByDealID(dealID = dealId)
            isFav.value = isExist
        } catch (e: Exception) {
            isFav.value = false
            e.printStackTrace()
        }
    }

    private suspend fun markDealsInFav(deal: DealsInfo, dealId: String) {
        try {
            favDealsDao.insertFavDeals(
                FavDeals(
                    dealID = dealId,
                    gameID = deal.gameInfo?.gameID!!,
                    thumb = deal.gameInfo.thumb,
                    title = deal.gameInfo.name,
                    steamAppId = deal.gameInfo.steamAppID
                )
            )
            isFav.value = true
            _dealFavStatus.emit(FavDealsState(true, "Added to Favourites"))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, "Failed to add to Favourites"))
            e.printStackTrace()
        }

    }

    private suspend fun removeFromFav(dealId: String) {
        try {
            val deal = favDealsDao.findByDealID(dealID = dealId)
            favDealsDao.deleteFavDeals(deal)
            isFav.value = false
            _dealFavStatus.emit(FavDealsState(false, "Removed from Favourites"))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, "Failed to remove from Favourites"))
            e.printStackTrace()
        }
    }

}

data class FavDealsState(
    val isFav: Boolean = false,
    val message: String = ""
)