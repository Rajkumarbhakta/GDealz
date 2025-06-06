package com.rkbapps.gdealz.worker

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rkbapps.gdealz.util.AppForegroundTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: NotificationWorkerRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        Log.d("NotificationWorker", "Running NotificationWorker")

        // Check if the app is in the foreground
        if (AppForegroundTracker.isAppInForeground()) {
            // If the app is in the foreground, do not send notifications
            return Result.success()
        }
        // get all the giveaways from the api and saved in the database
        val savedGiveaways = repository.getSavedGiveaways()
        if (savedGiveaways.data.isNullOrEmpty()) {
            // If there are no saved giveaways, return failure
            return Result.success()
        }
        val giveaways = repository.getGiveaways()

        // If both the API and saved giveaways are empty, return failure
        if (giveaways.data.isNullOrEmpty()) {
            return Result.success()
        }
        // If either the API or saved giveaways are not empty, proceed with processing
        val apiGiveaways = giveaways.data
        val savedGiveawaysList = savedGiveaways.data

        // Get new giveaways by comparing API giveaways with saved giveaways
        val newGiveaways = repository.getNewGiveaways(apiGiveaways, savedGiveawaysList)

        // If there are new giveaways, save them to the database and send notifications
        if (!newGiveaways.data.isNullOrEmpty()) {
            repository.sendNotificationForNewGiveaways(context,newGiveaways = newGiveaways.data)
            repository.saveGiveaways(apiGiveaways)
            return Result.success()
        } else {
            return Result.success()
        }
    }


    companion object {
        fun scheduleGiveawayWorker(context: Context) {
            Log.d("NotificationWorkerRepo", "Scheduling giveaway worker")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(20, TimeUnit.MINUTES)
                .setConstraints(constraints).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "GiveawayWorker",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // KEEP prevents duplicate workers
                workRequest
            )
        }
    }


}