package com.rkbapps.gdealz.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rkbapps.gdealz.db.entity.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store:Store)

    @Query("delete from store")
    suspend fun deleteAll()

    @Query("select * from store where storeID =:id")
    suspend fun findById(id:String):Store

    @Query("select * from store")
    suspend fun findAll():Flow<List<Store>>

}