package com.rkbapps.gdealz.ui.screens.dealslookup.repository

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.DealsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DealLookupRepository @Inject constructor(
    private val api: ApiInterface,
    private val storeDao: StoreDao,
    private val favDealsDao: FavDealsDao
) {
    private val _dealsData = MutableStateFlow<DealsInfo>(DealsInfo())
    val gameData: StateFlow<DealsInfo> = _dealsData

    private val isFav = mutableStateOf(false)
    val isFavDeal: State<Boolean> = isFav

    private val _dealFavStatus = MutableStateFlow<FavDealsState>(FavDealsState())
    val dealFavStatus: StateFlow<FavDealsState> = _dealFavStatus

    private val _storeData = MutableStateFlow<Store?>(null)
    val storeData: StateFlow<Store?> = _storeData


    suspend fun getDealsInfo(id: String) {
        try {
            val response = api.getDealsInfo(id = id)
//            val response = api.getDealsInfo()
//            Log.d("DEALSLOOKUP", "${response.raw()}")
            if (response.isSuccessful) {
//                response.raw()
                _dealsData.emit(response.body()!!)
//                Log.d("DEALSLOOKUP", "${ response.body() }")
                response.body()?.gameInfo?.storeID?.let { getStoreInfo(it) }
            } else {
                Log.d("DEALSLOOKUP", "${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("DEALSLOOKUP", "Error", e)
        }
    }


    private fun getStoreInfo(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val store = storeDao.findById(id)
                Log.d("DEALSLOOKUP", "Store: $store")
                _storeData.emit(store)
            } catch (e: Exception) {
                _storeData.emit(null)
                e.printStackTrace()
            }
        }

    }

    fun markFavDeals(deal: DealsInfo, dealId: String) {
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

    fun isDealFav(dealId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isExist = favDealsDao.isExistsByDealID(dealID = dealId)
                isFav.value = isExist
            } catch (e: Exception) {
                isFav.value = false
                e.printStackTrace()
            }
        }
    }

    private fun markDealsInFav(deal: DealsInfo, dealId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                favDealsDao.insertFavDeals(
                    FavDeals(
                        dealID = dealId,
                        gameID = deal.gameInfo?.gameID!!,
                        thumb = deal.gameInfo.thumb,
                        title = deal.gameInfo.name
                    )
                )
                isFav.value = true
                _dealFavStatus.emit(FavDealsState(true, "Added to Favourites"))
            } catch (e: Exception) {
                _dealFavStatus.emit(FavDealsState(false, "Failed to add to Favourites"))
                e.printStackTrace()
            }
        }
    }

    private fun removeFromFav(dealId: String) {
        CoroutineScope(Dispatchers.IO).launch {
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

}

data class FavDealsState(
    val isFav: Boolean = false,
    val message: String = ""
)