package com.rkbapps.gdealz.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.Coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.network.api.GamePowerApi
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NotificationWorkerRepository @Inject constructor(
    private val giveawaysDao: GiveawaysDao,
    private val gamePowerApi: GamePowerApi
) {
    suspend fun getGiveaways(): UiState<List<Giveaway>> {
        Log.d("NotificationWorkerRepo", "Fetching giveaways from API")
        val response = safeApiCall { gamePowerApi.getGiveawayByFilter() }
        return when (response) {
            is NetworkResponse.Error.HttpError -> {
                UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                UiState(error = "Unable to connect please check your internet connection.")
            }

            NetworkResponse.Error.UnknownError -> {
                UiState(error = "No active giveaways available at the moment, please try again later.")
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                if (!data.isNullOrEmpty()) {
                    UiState(data = data)
                } else {
                    UiState(error = "No active giveaways available at the moment, please try again later.")
                }
            }
        }
    }

    suspend fun getSavedGiveaways(): UiState<List<Giveaway>> {
        Log.d("NotificationWorkerRepo", "Fetching saved giveaways from database")
        return try {
            val giveaways = giveawaysDao.getAllGiveaways()
            if (giveaways.isNotEmpty()) {
                UiState(data = giveaways)
            } else {
                UiState(error = "No saved giveaways found.")
            }
        } catch (_: Exception) {
            UiState(error = "Error fetching saved giveaways.")
        }
    }

    fun getNewGiveaways(
        giveaways: List<Giveaway>,
        savedGiveaways: List<Giveaway>
    ): UiState<List<Giveaway>> {
        Log.d("NotificationWorkerRepo", "Comparing API giveaways with saved giveaways")
        val giveawayApiData = giveaways
        val giveawayDbData = savedGiveaways
        val newGiveaways = giveawayApiData.filter { apiGiveaway ->
            giveawayDbData.none { dbGiveaway -> dbGiveaway.id == apiGiveaway.id }
        }
        return if (newGiveaways.isNotEmpty()) {
            UiState(data = newGiveaways)
        } else {
            UiState(error = "No new giveaways found.")
        }
    }

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    suspend fun sendNotificationForNewGiveaways(
        context: Context,
        newGiveaways: List<Giveaway>
    ) {

        Log.d("NotificationWorkerRepo", "Sending notifications for new giveaways ${newGiveaways.size}")
        val notificationManager = NotificationManagerCompat.from(context)

        createNotificationChannel(notificationManager)

        newGiveaways.forEachIndexed { index, giveaway ->
            Log.d("NotificationWorkerRepo", "Sending notification for giveaway: ${giveaway.title}")
            val notificationId = System.currentTimeMillis().toInt() + index
            val notification = NotificationCompat.Builder(context, "giveaway_channel_id")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("New Giveaway: ${giveaway.title}")
                .setContentText("Check out this giveaway worth ${giveaway.worth}")
                .setChannelId("giveaway_channel_id")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            // Load network image into Bitmap using Coil
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val request = ImageRequest.Builder(context)
                        .data(giveaway.thumbnail)
                        .allowHardware(false) // Needed to get Bitmap
                        .build()

                    val result = imageLoader(context).execute(request)
                    if (result is SuccessResult) {
                        (result.drawable as? BitmapDrawable)?.bitmap
                    } else null
                } catch (e: Exception) {
                    null
                }
            }

            if (bitmap != null) {
                notification.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                )
            }

            notificationManager.notify(notificationId, notification.build())
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }



    suspend fun saveGiveaways(giveaways: List<Giveaway>) {
        try {
            Log.d("NotificationWorkerRepo", "Saving giveaways to database")
            giveawaysDao.replaceGiveaways(giveaways)
        }catch (_: Exception){

        }
    }


    companion object{
        const val CHANNEL_ID = "giveaway_channel_id"
        const val CHANNEL_NAME = "Giveaway Notifications"
        const val CHANNEL_DESCRIPTION = "Notifies when new giveaways are available"
        const val IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }
}