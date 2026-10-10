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

    @Query("SELECT * FROM giveaways where id=:giveawayId")
    suspend fun getGiveaway(giveawayId: Int): Giveaway

    @Query("SELECT * FROM giveaways order by publishedDate DESC")
    fun getGiveawaysByOrder(): Flow<List<Giveaway>>

    @Query("select platforms from giveaways order by publishedDate DESC")
    fun getPlatforms(): Flow<List<String>>

    @Query("select type from giveaways order by publishedDate DESC")
    fun getTypes(): Flow<List<String>>

    @Query("SELECT * FROM giveaways")
    suspend fun getAllGiveaways(): List<Giveaway>

    @Query("delete FROM giveaways")
    suspend fun deleteAllGiveaways()


    @Query("SELECT * FROM giveaways WHERE isClaimed =:isClaimed and type not in ('Other') order by publishedDate DESC")
    fun getGiveawaysByClaimed(isClaimed: Boolean): Flow<List<Giveaway>>

    @Query("DELETE FROM giveaways WHERE isClaimed =:isClaimed")
    suspend fun deleteGiveawaysByClaimed(isClaimed: Boolean)


    @Query("select * from giveaways where id=:id")
    suspend fun getGiveawayById(id: Int): Giveaway?

    @Query("SELECT id FROM giveaways WHERE isClaimed = 1")
    suspend fun getClaimedGiveawayIds(): List<Int>

    /**
     * Refreshes the cached giveaways while preserving rows the user has already claimed.
     *
     * The REPLACE insert would otherwise overwrite claimed rows and reset [Giveaway.isClaimed],
     * so incoming rows that match a claimed id are dropped before inserting.
     */
    @Transaction
    suspend fun replaceGiveaways(giveaways: List<Giveaway>) {
        val claimedIds = getClaimedGiveawayIds().toSet()
        deleteGiveawaysByClaimed(false)
        insertGiveaways(giveaways.filterNot { it.id in claimedIds })
    }

}