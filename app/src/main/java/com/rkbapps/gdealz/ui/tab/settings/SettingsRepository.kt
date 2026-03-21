package com.rkbapps.gdealz.ui.tab.settings

import android.annotation.SuppressLint
import android.content.Context
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.util.FavStoreIds
import com.rkbapps.gdealz.worker.NotificationWorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val prefManager: PreferenceManager,
    private val giveawaysDao: GiveawaysDao,
    private val notificationWorkerRepository: NotificationWorkerRepository
) {

    val isSystemTheme = prefManager.getBooleanPreference(PreferenceManager.IS_USE_SYSTEM_THEME,true)
    val isDarkTheme = prefManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME)
    val selectedCountry = prefManager.getStringPreference(PreferenceManager.SELECTED_COUNTRY,)
    val isNsfw = prefManager.getBooleanPreference(PreferenceManager.IS_NSFW_ALLOWED)


    suspend fun updateIsSystemTheme(value: Boolean)=prefManager.saveBooleanPreference(PreferenceManager.IS_USE_SYSTEM_THEME,value)
    suspend fun updateTheme(value:Boolean) = prefManager.saveBooleanPreference(PreferenceManager.IS_DARK_THEME,value)
    suspend fun updateCountry(value:String) = prefManager.saveStringPreference(PreferenceManager.SELECTED_COUNTRY,value)
    suspend fun updateNsfwContentAllowance(value:Boolean) = prefManager.saveBooleanPreference(PreferenceManager.IS_NSFW_ALLOWED,value)






    @SuppressLint("MissingPermission")
    suspend fun sendNotification(context: Context){
        val giveaway = giveawaysDao.getAllGiveaways().take(2)
        withContext(Dispatchers.Main){
            notificationWorkerRepository.sendNotificationForNewGiveaways(context,giveaway)
        }
    }







}