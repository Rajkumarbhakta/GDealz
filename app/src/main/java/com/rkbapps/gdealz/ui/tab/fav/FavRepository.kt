package com.rkbapps.gdealz.ui.tab.fav

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.util.FavStoreIds
import com.rkbapps.gdealz.util.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FavRepository @Inject constructor(
    private val favDb: FavDealsDao,
    private val prefManager: PreferenceManager,
    @ApplicationContext private val context: Context
) {

    val favList = favDb.selectAllFavDeals()

    val favStoreList = prefManager.getObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds::class.java)

    private val _dealsData = MutableStateFlow(UiState<DealsInfo>())
    val dealsData = _dealsData.asStateFlow()

    private val isFav = mutableStateOf(false)
    val isFavDeal: State<Boolean> = isFav

    private val _dealFavStatus = MutableStateFlow(FavDealsState())
    val dealFavStatus: StateFlow<FavDealsState> = _dealFavStatus



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
            val isExist = favDb.isExistsByDealID(dealID = dealId)
            isFav.value = isExist
        } catch (e: Exception) {
            isFav.value = false
            e.printStackTrace()
        }
    }

    private suspend fun markDealsInFav(deal: DealsInfo, dealId: String) {
        try {
            favDb.insertFavDeals(
                FavDeals(
                    dealID = dealId,
                    gameID = deal.gameInfo?.gameID!!,
                    slug = deal.gameInfo.gameID,
                    thumb = deal.gameInfo.thumb,
                    title = deal.gameInfo.name,
                    steamAppId = deal.gameInfo.steamAppID
                )
            )
            isFav.value = true
            _dealFavStatus.emit(FavDealsState(true, context.getString(R.string.added_to_favourites)))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.failed_to_add_to_favourites)))
            e.printStackTrace()
        }

    }

    private suspend fun removeFromFav(dealId: String) {
        try {
            val deal = favDb.findByDealID(dealID = dealId)
            favDb.deleteFavDeals(deal)
            isFav.value = false
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.removed_from_favourites)))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.failed_to_remove_from_favourites)))
            e.printStackTrace()
        }
    }


    suspend fun deleteAFav(favDeal: FavDeals){
        try {
            favDb.deleteFavDeals(favDeal)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    suspend fun deleteAllFav(){
        try {
            favDb.deleteAllFavDeals()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }



    suspend fun markStoreAsFav(storeId: Int){
        val favStoreList = prefManager.getObjectSynchronous(PreferenceManager.FAV_STORE_IDS, FavStoreIds::class.java)
        favStoreList?.let {
            val list = it.ids.toMutableList()
            if (!list.contains(storeId)){
                list.add(storeId)
                prefManager.saveObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds(list))
            }else{
                removeStoreFromFav(storeId,favStoreList)
            }
            return
        }
        prefManager.saveObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds(listOf(storeId)))
    }
    private suspend fun removeStoreFromFav(storeId: Int,favStoreList: FavStoreIds? = null){
        if ( favStoreList?.ids?.contains(storeId) ==true ){
            val list = favStoreList.ids.toMutableList()
            list.remove(storeId)
            prefManager.saveObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds(list))
        }
    }



}


data class FavDealsState(
    val isFav: Boolean = false,
    val message: String = ""
)