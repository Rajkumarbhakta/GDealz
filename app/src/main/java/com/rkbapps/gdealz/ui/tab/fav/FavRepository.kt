package com.rkbapps.gdealz.ui.tab.fav

import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import javax.inject.Inject

class FavRepository @Inject constructor(
    private val favDb: FavDealsDao
) {

    val favList = favDb.selectAllFavDeals()

    suspend fun deleteAFav(favDeal: FavDeals){
        try {
            favDb.deleteFavDeals(favDeal)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }




}