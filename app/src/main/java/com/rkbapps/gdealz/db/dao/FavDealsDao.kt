package com.rkbapps.gdealz.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rkbapps.gdealz.db.entity.FavDeals
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDealsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavDeals(favDeals: FavDeals)

    @Delete
    suspend fun deleteFavDeals(favDeals: FavDeals)

    @Query("select * from `fav-deals`")
    fun selectAllFavDeals():Flow<List<FavDeals>>

    @Query("select * from `fav-deals` where dealID=:dealID")
    suspend fun findByDealID(dealID:String):FavDeals

    @Query("delete from `fav-deals` where dealID=:dealID")
    suspend fun deleteByDealID(dealID:Int)

    @Query("select * from `fav-deals` where id =:id")
    suspend fun findById(id:Int):FavDeals

    @Query("select exists(select * from `fav-deals` where id =:id)")
    suspend fun isExists(id:Int):Boolean

    @Query("select exists(select * from `fav-deals` where dealID=:dealID)")
    suspend fun isExistsByDealID(dealID:String):Boolean

    @Query("delete from `fav-deals`")
    suspend fun deleteAllFavDeals()




}