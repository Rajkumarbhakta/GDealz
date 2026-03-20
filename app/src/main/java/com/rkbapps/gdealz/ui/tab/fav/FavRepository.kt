package com.rkbapps.gdealz.ui.tab.fav

import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.util.FavStoreIds
import javax.inject.Inject

class FavRepository @Inject constructor(
    private val favDb: FavDealsDao,
    private val prefManager: PreferenceManager,
    ) {

    val favList = favDb.selectAllFavDeals()

    val favStoreList = prefManager.getObject(PreferenceManager.FAV_STORE_IDS,FavStoreIds::class.java)


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
        val favStoreList = prefManager.getObjectSynchronous(PreferenceManager.FAV_STORE_IDS,
            FavStoreIds::class.java)
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