package com.rkbapps.gdealz.worker.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import coil.Coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.activity.MainActivity
import com.rkbapps.gdealz.db.NotificationPreferencesRepository
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.models.NotificationPreferences
import com.rkbapps.gdealz.navigation.DeepLinkBasePaths
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.GamePowerApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.util.UiState
import com.rkbapps.gdealz.util.matchesFilter
import com.rkbapps.gdealz.util.meetsMinWorth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NotificationWorkerRepository @Inject constructor(
    private val giveawaysDao: GiveawaysDao,
    private val gamePowerApi: GamePowerApi,
    private val notificationPreferences: NotificationPreferencesRepository,
    @ApplicationContext private val context: Context
) {

    suspend fun preferences(): NotificationPreferences = notificationPreferences.current()

    /**
     * Narrows newly seen giveaways down to the ones the user asked to hear about.
     *
     * Applied only to what gets notified - never to what gets saved, since the saved set is the
     * "already seen" baseline. Filtering the baseline would freeze it and dump a backlog the next
     * time the user widened a filter.
     */
    fun filterForNotification(
        giveaways: List<Giveaway>,
        prefs: NotificationPreferences
    ): List<Giveaway> = giveaways.filter {
        it.matchesFilter(prefs.platforms, prefs.types) && it.meetsMinWorth(prefs.minWorth)
    }

    /**
     * Refreshes the "already seen" baseline without notifying.
     *
     * Used when giveaway notifications are switched back on: without it, everything that appeared
     * while they were off would look new and arrive at once.
     */
    suspend fun primeSeenGiveaways() {
        val giveaways = getGiveaways().data ?: return
        saveGiveaways(giveaways)
    }
    suspend fun getGiveaways(): UiState<List<Giveaway>> {
        Log.d("NotificationWorkerRepo", "Fetching giveaways from API")
        val response = safeApiCall { gamePowerApi.getGiveawayByFilter() }
        return when (response) {
            is NetworkResponse.Error.HttpError -> {
                UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }

            NetworkResponse.Error.NetworkError -> {
                UiState(error = context.getString(R.string.unable_to_connect))
            }

            NetworkResponse.Error.UnknownError -> {
                UiState(error = context.getString(R.string.no_active_giveaways))
            }

            is NetworkResponse.Success<List<Giveaway>?> -> {
                val data = response.value
                if (!data.isNullOrEmpty()) {
                    UiState(data = data)
                } else {
                    UiState(error = context.getString(R.string.no_active_giveaways))
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    suspend fun sendNotificationForNewGiveaways(context: Context, newGiveaways: List<Giveaway>) {
        if (newGiveaways.isEmpty()) return
        Log.d("NotificationWorkerRepo", "Sending notifications for new giveaways ${newGiveaways.size}")
        val notificationManager = NotificationManagerCompat.from(context)

        NotificationChannels.createGiveawayChannels(context)

        newGiveaways.forEach { giveaway ->
            Log.d("NotificationWorkerRepo", "Sending notification for giveaway: ${giveaway.title}")
            val notification = buildNotification(context, giveaway)
            // The giveaway id is stable, so a repeat replaces its own notification instead of
            // stacking a duplicate. Tagged to keep the id space separate from price drops.
            notificationManager.notify(NOTIFICATION_TAG, giveaway.id, notification)
        }

        if (newGiveaways.size > 1) {
            notificationManager.notify(
                NOTIFICATION_TAG,
                SUMMARY_NOTIFICATION_ID,
                buildSummaryNotification(context, newGiveaways)
            )
        }
    }

    /**
     * Collapses a batch into one entry. Matters most after quiet hours, when a whole night of
     * giveaways is delivered at once.
     */
    private fun buildSummaryNotification(context: Context, giveaways: List<Giveaway>): Notification =
        NotificationCompat.Builder(context, NotificationChannels.channelIdFor(giveaways.first().type))
            .setSmallIcon(R.drawable.console)
            .setContentTitle(context.getString(R.string.new_giveaways_summary, giveaways.size))
            .setStyle(
                NotificationCompat.InboxStyle().also { style ->
                    giveaways.take(5).forEach { style.addLine(it.title) }
                }
            )
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()

    /**
     * Posts a single notification so the user can confirm delivery reaches this device.
     *
     * Deliberately synthetic rather than replaying a cached giveaway: the cache is empty until the
     * Free tab has been opened once, and a test that silently does nothing is worse than no test.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendTestNotification(context: Context) {
        NotificationChannels.createGiveawayChannels(context)
        val notification = NotificationCompat.Builder(context, NotificationChannels.GAME_CHANNEL_ID)
            .setSmallIcon(R.drawable.console)
            .setContentTitle(context.getString(R.string.test_notification_title))
            .setContentText(context.getString(R.string.test_notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_TAG, TEST_NOTIFICATION_ID, notification)
    }

    private fun claimPendingIntent(context: Context, giveawayId: Int, code: Int): PendingIntent {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${DeepLinkBasePaths.FREE_GAME_DETAILS}/$giveawayId".toUri(),
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(code, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private suspend fun buildNotification(context: Context, giveaway: Giveaway): Notification{

        val pendingIntent = claimPendingIntent(context, giveaway.id, giveaway.id)

        val notification = NotificationCompat.Builder(context, NotificationChannels.channelIdFor(giveaway.type))
            .setSmallIcon(R.drawable.console)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setContentTitle(context.getString(R.string.new_giveaway, giveaway.title))
            .setContentText(context.getString(R.string.check_out_giveaway, giveaway.worth))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)


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
                e.printStackTrace()
                null
            }
        }

        if (bitmap != null) {
            notification.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
            )
        }

       /* giveaway.openGiveawayUrl.let {giveawayUrl->
            val claimPendingIntent = claimPendingIntent(context, giveaway.id, index)
            notification.addAction(
                R.drawable.console,
                "Claim",
                claimPendingIntent
            )
        }*/

        return notification.build()
    }




    suspend fun saveGiveaways(giveaways: List<Giveaway>) {
        try {
            Log.d("NotificationWorkerRepo", "Saving giveaways to database")
            giveawaysDao.replaceGiveaways(giveaways)
        }catch (_: Exception){

        }
    }


    companion object{
        const val NOTIFICATION_TAG = "giveaway"
        const val NOTIFICATION_GROUP_KEY = "com.rkbapps.gdealz.GIVEAWAYS"
        const val SUMMARY_NOTIFICATION_ID = -1
        const val TEST_NOTIFICATION_ID = -2

        const val GAME_GIVEAWAY_ID = "giveawayId"

    }
}