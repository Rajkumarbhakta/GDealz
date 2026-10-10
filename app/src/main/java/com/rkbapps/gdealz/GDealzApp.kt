package com.rkbapps.gdealz

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rkbapps.gdealz.db.NotificationPreferencesRepository
import com.rkbapps.gdealz.worker.notification.NotificationChannels
import com.rkbapps.gdealz.worker.notification.NotificationWorker
import com.rkbapps.gdealz.worker.price_tracker.PriceDropWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GDealzApp() :Application(),Configuration.Provider{

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationPreferences: NotificationPreferencesRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.createGiveawayChannels(this)
        appScope.launch { migrateLegacyChannel() }
        NotificationWorker.scheduleGiveawayWorker(this)
        PriceDropWorker.schedulePriceDropWorker(this)
    }

    /**
     * Retires the single pre-split giveaway channel.
     *
     * If the user had blocked it, deleting it would quietly resume the notifications they turned
     * off, so that choice is carried over to the in-app master toggle first.
     */
    private suspend fun migrateLegacyChannel() {
        if (NotificationChannels.wasLegacyChannelBlocked(this)) {
            notificationPreferences.setGiveawayEnabled(false)
        }
        NotificationChannels.deleteLegacyChannel(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()


}
