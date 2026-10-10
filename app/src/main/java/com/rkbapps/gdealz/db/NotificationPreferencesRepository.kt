package com.rkbapps.gdealz.db

import androidx.datastore.preferences.core.Preferences
import com.google.gson.Gson
import com.rkbapps.gdealz.models.NotificationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads and writes the user's notification choices.
 *
 * Each value is stored under its own DataStore key rather than as one JSON blob: a blob would force
 * a read-modify-write on every change, which loses an update when two toggles are flipped in quick
 * succession. `dataStore.edit {}` per key is atomic.
 *
 * The two string lists are still JSON, since DataStore has no list type worth using here.
 */
@Singleton
class NotificationPreferencesRepository @Inject constructor(
    private val prefManager: PreferenceManager,
    private val gson: Gson,
) {

    val preferences: Flow<NotificationPreferences> =
        prefManager.preferencesFlow.map { it.toNotificationPreferences() }

    /** One-shot read, for WorkManager workers that cannot observe a flow. */
    suspend fun current(): NotificationPreferences = preferences.first()

    suspend fun setGiveawayEnabled(value: Boolean) =
        prefManager.saveBooleanPreference(PreferenceManager.IS_GIVEAWAY_NOTIFICATION_ENABLED, value)

    suspend fun setPriceDropEnabled(value: Boolean) =
        prefManager.saveBooleanPreference(PreferenceManager.IS_PRICE_DROP_NOTIFICATION_ENABLED, value)

    suspend fun setPlatforms(value: List<String>) =
        prefManager.saveStringPreference(PreferenceManager.NOTIFICATION_PLATFORMS, gson.toJson(value))

    suspend fun setTypes(value: List<String>) =
        prefManager.saveStringPreference(PreferenceManager.NOTIFICATION_TYPES, gson.toJson(value))

    suspend fun setMinWorth(value: Int) =
        prefManager.saveIntPreference(PreferenceManager.NOTIFICATION_MIN_WORTH, value)

    suspend fun setQuietHoursEnabled(value: Boolean) =
        prefManager.saveBooleanPreference(PreferenceManager.IS_QUIET_HOURS_ENABLED, value)

    suspend fun setQuietHours(start: Int, end: Int) {
        prefManager.saveIntPreference(PreferenceManager.QUIET_HOURS_START, start)
        prefManager.saveIntPreference(PreferenceManager.QUIET_HOURS_END, end)
    }

    private fun Preferences.toNotificationPreferences() = NotificationPreferences(
        giveawayEnabled = this[PreferenceManager.IS_GIVEAWAY_NOTIFICATION_ENABLED] ?: true,
        priceDropEnabled = this[PreferenceManager.IS_PRICE_DROP_NOTIFICATION_ENABLED] ?: true,
        platforms = decodeList(this[PreferenceManager.NOTIFICATION_PLATFORMS]),
        types = decodeList(this[PreferenceManager.NOTIFICATION_TYPES]),
        minWorth = this[PreferenceManager.NOTIFICATION_MIN_WORTH] ?: 0,
        quietHoursEnabled = this[PreferenceManager.IS_QUIET_HOURS_ENABLED] ?: false,
        quietHoursStart = this[PreferenceManager.QUIET_HOURS_START] ?: DEFAULT_QUIET_START,
        quietHoursEnd = this[PreferenceManager.QUIET_HOURS_END] ?: DEFAULT_QUIET_END,
    )

    /** `Array<String>` avoids needing a `TypeToken`, which `List<String>` would require. */
    private fun decodeList(json: String?): List<String> = json?.let {
        runCatching { gson.fromJson(it, Array<String>::class.java)?.toList() }.getOrNull()
    }.orEmpty()

    companion object {
        const val DEFAULT_QUIET_START = 22
        const val DEFAULT_QUIET_END = 8
    }
}
