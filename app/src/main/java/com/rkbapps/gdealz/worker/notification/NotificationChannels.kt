package com.rkbapps.gdealz.worker.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.rkbapps.gdealz.R

/**
 * Giveaway notification channels, split by content type so the system notification settings offer
 * the same granularity the in-app filters do - a user can mute DLC from Android's own UI while
 * leaving free games audible.
 *
 * The in-app filters decide whether a notification is posted at all; the channel is the user's
 * system-level override on top of that.
 */
object NotificationChannels {

    const val GROUP_ID = "giveaways"

    const val GAME_CHANNEL_ID = "giveaway_game_channel_id"
    const val DLC_CHANNEL_ID = "giveaway_dlc_channel_id"
    const val OTHER_CHANNEL_ID = "giveaway_other_channel_id"

    /** The single channel used before notifications were split by type. */
    const val LEGACY_CHANNEL_ID = "giveaway_channel_id"

    fun channelIdFor(type: String): String = when (type.lowercase()) {
        "game" -> GAME_CHANNEL_ID
        "dlc", "loot", "early access" -> DLC_CHANNEL_ID
        else -> OTHER_CHANNEL_ID
    }

    /**
     * Creates the channels up front, so they are visible in system settings before the first
     * notification fires. Creating an existing channel is a no-op, so this is safe on every launch.
     */
    fun createGiveawayChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = NotificationManagerCompat.from(context)

        manager.createNotificationChannelGroup(
            NotificationChannelGroup(GROUP_ID, context.getString(R.string.channel_group_giveaways))
        )

        listOf(
            GAME_CHANNEL_ID to R.string.channel_free_games,
            DLC_CHANNEL_ID to R.string.channel_free_dlc,
            OTHER_CHANNEL_ID to R.string.channel_other_giveaways,
        ).forEach { (id, nameRes) ->
            manager.createNotificationChannel(
                NotificationChannel(id, context.getString(nameRes), NotificationManager.IMPORTANCE_HIGH)
                    .apply { group = GROUP_ID }
            )
        }
    }

    /**
     * Whether the user had blocked the old single giveaway channel.
     *
     * Deleting a blocked channel would silently resume delivery they had turned off, so the caller
     * carries that choice over to the in-app master toggle instead.
     */
    fun wasLegacyChannelBlocked(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val existing = NotificationManagerCompat.from(context)
            .getNotificationChannel(LEGACY_CHANNEL_ID) ?: return false
        return existing.importance == NotificationManager.IMPORTANCE_NONE
    }

    fun deleteLegacyChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        NotificationManagerCompat.from(context).deleteNotificationChannel(LEGACY_CHANNEL_ID)
    }
}
