package com.rkbapps.gdealz

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rkbapps.gdealz.worker.notification.NotificationWorker
import com.rkbapps.gdealz.worker.price_tracker.PriceDropWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GDealzApp() :Application(),Configuration.Provider{

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        NotificationWorker.scheduleGiveawayWorker(this)
        PriceDropWorker.schedulePriceDropWorker(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()


}