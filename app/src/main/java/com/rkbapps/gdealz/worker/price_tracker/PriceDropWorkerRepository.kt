package com.rkbapps.gdealz.worker.price_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.Coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.activity.MainActivity
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PriceDropWorkerRepository @Inject constructor(
    private val favDealsDao: FavDealsDao,
    private val isThereAnyDealApi: IsThereAnyDealApi,
    @ApplicationContext private val context: Context
) {

    @SuppressLint("MissingPermission")
    suspend fun checkPricesAndNotify() {
        Log.d("PriceDropWorkerRepo", "Checking prices for favorite deals")
        val favDeals = favDealsDao.getAllFavDeals()
        if (favDeals.isEmpty()) return

        val gameIds = favDeals.map { it.gameID }
        try {
            val priceDetails = isThereAnyDealApi.getPrices(gameIds = gameIds)
            
            for (favDeal in favDeals) {
                val priceDetail = priceDetails.find { it.id == favDeal.gameID } ?: continue
                
                // Find the minimum price among all deals for this game
                val currentDeals = priceDetail.deals
                if (currentDeals.isEmpty()) continue
                
                val minDeal = currentDeals.minByOrNull { it.price?.amount ?: Double.MAX_VALUE } ?: continue
                val newLowestPrice = minDeal.price?.amount ?: continue
                val actualPrice = minDeal.regular?.amount
                val discountPercentage = minDeal.cut?.toDouble()
                val currencySymbol = minDeal.price?.currency

                val oldLowestPrice = favDeal.currentlyLowestPrice

                if (oldLowestPrice == null || newLowestPrice != oldLowestPrice) {
                    Log.d("PriceDropWorkerRepo", "Price drop detected for ${favDeal.title}: $oldLowestPrice -> $newLowestPrice")
                    
                    // Update database
                    val updatedFavDeal = favDeal.copy(
                        actualPrice = actualPrice,
                        currentlyLowestPrice = newLowestPrice,
                        discountPercentage = discountPercentage,
                        currencySymbol = currencySymbol
                    )
                    favDealsDao.updateFavDeals(updatedFavDeal)
                    

                    sendPriceDropNotification(updatedFavDeal)
                }
            }
        } catch (e: Exception) {
            Log.e("PriceDropWorkerRepo", "Error checking prices", e)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun sendPriceDropNotification(favDeal: FavDeals) {
        val notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel(notificationManager)

        val notificationId = favDeal.gameID.hashCode()
        val notification = buildNotification(favDeal)

        with(notificationManager) {
            notify(notificationId, notification)
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

    private suspend fun buildNotification(favDeal: FavDeals): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            favDeal.gameID.hashCode(), 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val priceText = "${favDeal.currencySymbol ?: ""}${favDeal.currentlyLowestPrice}"
        val contentText = context.getString(R.string.price_drop_message, favDeal.title, priceText)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.console)
            .setContentTitle(context.getString(R.string.price_drop_title))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Load image if available
        val bitmap = withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(favDeal.thumb)
                    .allowHardware(false)
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
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        return notificationBuilder.build()
    }

    companion object {
        const val CHANNEL_ID = "price_drop_channel_id"
        const val CHANNEL_NAME = "Price Drop Notifications"
        const val CHANNEL_DESCRIPTION = "Notifies when favorite game prices drop"
        
        @RequiresApi(Build.VERSION_CODES.N)
        const val IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }
}
