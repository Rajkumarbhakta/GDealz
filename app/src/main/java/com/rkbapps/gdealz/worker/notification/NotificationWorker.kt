package com.rkbapps.gdealz.worker.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rkbapps.gdealz.util.AppForegroundTracker
import com.rkbapps.gdealz.util.isWithinQuietHours
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

        val primeOnly = inputData.getBoolean(KEY_PRIME_ONLY, false)

        if (primeOnly) {
            // Refresh the "already seen" baseline quietly, so re-enabling notifications does not
            // deliver everything that appeared while they were off.
            Log.d("NotificationWorker", "Priming seen giveaways")
            repository.primeSeenGiveaways()
            return Result.success()
        }

        val prefs = repository.preferences()

        if (!prefs.giveawayEnabled) {
            Log.d("NotificationWorker", "Giveaway notifications disabled, skipping")
            return Result.success()
        }

        // Returning before the fetch means nothing is saved, so the first run after quiet hours
        // still sees the backlog as new and delivers it then.
        if (prefs.quietHoursEnabled &&
            isWithinQuietHours(prefs.quietHoursStart, prefs.quietHoursEnd)
        ) {
            Log.d("NotificationWorker", "Within quiet hours, skipping")
            return Result.success()
        }

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
            // Only what the user asked to hear about is notified. The save below deliberately uses
            // the unfiltered list, since it is the baseline of what has already been seen.
            val toNotify = repository.filterForNotification(newGiveaways.data, prefs)
            repository.sendNotificationForNewGiveaways(context, newGiveaways = toNotify)
            repository.saveGiveaways(apiGiveaways)
            return Result.success()
        } else {
            return Result.success()
        }
    }


    companion object {
        const val WORK_NAME = "GiveawayWorker"
        const val PRIME_WORK_NAME = "GiveawayWorkerPrime"
        const val KEY_PRIME_ONLY = "prime_only"

        fun cancelGiveawayWorker(context: Context) {
            Log.d("NotificationWorkerRepo", "Cancelling giveaway worker")
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        /**
         * Refreshes the "already seen" baseline in the background.
         *
         * Runs as a worker rather than from a ViewModel scope so it survives the user leaving the
         * settings screen mid-fetch - if it were cancelled, the next periodic run would flood.
         */
        fun primeSeenGiveaways(context: Context) {
            Log.d("NotificationWorkerRepo", "Enqueuing prime run")
            val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInputData(workDataOf(KEY_PRIME_ONLY to true))
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                PRIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun scheduleGiveawayWorker(context: Context) {
            Log.d("NotificationWorkerRepo", "Scheduling giveaway worker")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(20, TimeUnit.MINUTES)
                .setConstraints(constraints).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }


}