package com.rkbapps.gdealz.worker.price_tracker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rkbapps.gdealz.util.AppForegroundTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PriceDropWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: PriceDropWorkerRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("PriceDropWorker", "Running PriceDropWorker")
        // Check if the app is in the foreground
//        if (AppForegroundTracker.isAppInForeground()) {
//            // If the app is in the foreground, do not send notifications
//            return Result.success()
//        }

        repository.checkPricesAndNotify()

        return Result.success()
    }

    companion object {
        fun schedulePriceDropWorker(context: Context) {
            Log.d("PriceDropWorker", "Scheduling price drop worker")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<PriceDropWorker>(5, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "PriceDropWorker",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest
            )
        }
    }
}
