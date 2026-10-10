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
import com.rkbapps.gdealz.util.isWithinQuietHours
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

        val prefs = repository.preferences()

        // Both guards sit here rather than in the send path: checkPricesAndNotify writes the new
        // price to the database before deciding whether to notify, so gating any later would
        // consume the drop silently and it would never be reported.
        if (!prefs.priceDropEnabled) {
            Log.d("PriceDropWorker", "Price drop notifications disabled, skipping")
            return Result.success()
        }
        if (prefs.quietHoursEnabled &&
            isWithinQuietHours(prefs.quietHoursStart, prefs.quietHoursEnd)
        ) {
            Log.d("PriceDropWorker", "Within quiet hours, skipping")
            return Result.success()
        }

        repository.checkPricesAndNotify()
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "PriceDropWorker"

        fun cancelPriceDropWorker(context: Context) {
            Log.d("PriceDropWorker", "Cancelling price drop worker")
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun schedulePriceDropWorker(context: Context) {
            Log.d("PriceDropWorker", "Scheduling price drop worker")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<PriceDropWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
