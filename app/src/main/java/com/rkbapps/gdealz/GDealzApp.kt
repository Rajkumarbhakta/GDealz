package com.rkbapps.gdealz

import android.app.Application
import com.rkbapps.gdealz.worker.NotificationWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GDealzApp:Application(){

    override fun onCreate() {
        super.onCreate()
        NotificationWorker.scheduleGiveawayWorker(this)
    }

}