package com.rkbapps.gdealz.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rkbapps.gdealz.models.Giveaway
import kotlinx.coroutines.flow.Flow


@Dao
interface GiveawaysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiveaways(giveaways: List<Giveaway>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiveaway(giveaways: Giveaway)

    @Update
    suspend fun updateGiveaway(giveaway: Giveaway)

    @Delete
    suspend fun deleteGiveaway(giveaway: Giveaway)

    @Query("SELECT * FROM giveaways")
    fun giveaways(): Flow<List<Giveaway>>

    @Query("SELECT * FROM giveaways order by publishedDate DESC")
    fun getGiveawaysByOrder(): Flow<List<Giveaway>>

    @Query("SELECT * FROM giveaways")
    suspend fun getAllGiveaways(): List<Giveaway>

    @Query("delete FROM giveaways")
    suspend fun deleteAllGiveaways()


    @Query("SELECT * FROM giveaways WHERE isClaimed =:isClaimed order by publishedDate DESC")
    fun getGiveawaysByClaimed(isClaimed: Boolean): Flow<List<Giveaway>>

    @Query("DELETE FROM giveaways WHERE isClaimed =:isClaimed")
    suspend fun deleteGiveawaysByClaimed(isClaimed: Boolean)

    @Transaction
    suspend fun replaceGiveaways(giveaways: List<Giveaway>) {
        deleteGiveawaysByClaimed(false)
        insertGiveaways(giveaways)
    }

}