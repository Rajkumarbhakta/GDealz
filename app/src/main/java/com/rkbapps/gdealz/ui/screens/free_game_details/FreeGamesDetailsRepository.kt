package com.rkbapps.gdealz.ui.screens.free_game_details

import android.annotation.SuppressLint
import android.content.Context
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.worker.notification.NotificationWorkerRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FreeGamesDetailsRepository @Inject constructor(
    private val giveawaysDao: GiveawaysDao,
    private val notification: NotificationWorkerRepository
) {

    suspend fun getGiveaway(giveawayId:Int) = giveawaysDao.getGiveaway(giveawayId)

    suspend fun markGiveawayAsClaimed(giveaway: Giveaway) {
        val update = giveaway.copy(isClaimed = true)
        giveawaysDao.updateGiveaway(update)
    }

    suspend fun markGiveawayAsUnClaimed(giveaway: Giveaway) {
        val update = giveaway.copy(isClaimed = false)
        giveawaysDao.updateGiveaway(update)
    }

    @SuppressLint("MissingPermission")
    suspend fun sendNotification(context: Context){
        val giveAway = giveawaysDao.getAllGiveaways()
        val notificationGiveaway = if (giveAway.count()>2) giveAway.take(2) else giveAway
        withContext(Dispatchers.Main){
            notification.sendNotificationForNewGiveaways(context,notificationGiveaway)
        }
    }


}