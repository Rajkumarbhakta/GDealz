package com.rkbapps.gdealz.ui.tab.settings.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.db.NotificationPreferencesRepository
import com.rkbapps.gdealz.models.NotificationPreferences
import com.rkbapps.gdealz.network.GiveawayPlatformNames
import com.rkbapps.gdealz.network.GiveawayTypes
import com.rkbapps.gdealz.worker.notification.NotificationWorker
import com.rkbapps.gdealz.worker.notification.NotificationWorkerRepository
import com.rkbapps.gdealz.worker.price_tracker.PriceDropWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: NotificationPreferencesRepository,
    private val notificationWorkerRepository: NotificationWorkerRepository,
) : ViewModel() {

    val preferences = repository.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NotificationPreferences()
    )

    val typeOptions = GiveawayTypes.ALL
    val platformOptions = GiveawayPlatformNames.ALL

    fun updateGiveawayEnabled(value: Boolean) = viewModelScope.launch {
        repository.setGiveawayEnabled(value)
        if (value) {
            NotificationWorker.scheduleGiveawayWorker(context)
            // Bring the "already seen" set up to date, otherwise everything that appeared while
            // notifications were off would be treated as new and arrive in one burst.
            NotificationWorker.primeSeenGiveaways(context)
        } else {
            NotificationWorker.cancelGiveawayWorker(context)
        }
    }

    fun updatePriceDropEnabled(value: Boolean) = viewModelScope.launch {
        repository.setPriceDropEnabled(value)
        if (value) {
            PriceDropWorker.schedulePriceDropWorker(context)
        } else {
            PriceDropWorker.cancelPriceDropWorker(context)
        }
    }

    fun updatePlatforms(value: List<String>) =
        viewModelScope.launch { repository.setPlatforms(value) }

    fun updateTypes(value: List<String>) =
        viewModelScope.launch { repository.setTypes(value) }

    fun updateMinWorth(value: Int) =
        viewModelScope.launch { repository.setMinWorth(value) }

    fun updateQuietHoursEnabled(value: Boolean) =
        viewModelScope.launch { repository.setQuietHoursEnabled(value) }

    fun updateQuietHours(start: Int, end: Int) =
        viewModelScope.launch { repository.setQuietHours(start, end) }

    @SuppressLint("MissingPermission")
    fun sendTestNotification() = notificationWorkerRepository.sendTestNotification(context)
}
