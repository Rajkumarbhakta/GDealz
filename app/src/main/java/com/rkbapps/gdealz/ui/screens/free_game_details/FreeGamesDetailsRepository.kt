package com.rkbapps.gdealz.ui.screens.free_game_details

import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import jakarta.inject.Inject

class FreeGamesDetailsRepository @Inject constructor(
    private val giveawaysDao: GiveawaysDao
) {

    suspend fun markGiveawayAsClaimed(giveaway: Giveaway) {
        val update = giveaway.copy(isClaimed = true)
        giveawaysDao.updateGiveaway(update)
    }

    suspend fun markGiveawayAsUnClaimed(giveaway: Giveaway) {
        val update = giveaway.copy(isClaimed = false)
        giveawaysDao.updateGiveaway(update)
    }


}