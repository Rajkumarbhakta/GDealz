package com.rkbapps.gdealz.worker.price_tracker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

class PriceDropWorkerFactory @Inject constructor(
    private val repository: PriceDropWorkerRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return PriceDropWorker(
            context = appContext,
            params = workerParameters,
            repository = repository
        )
    }
}