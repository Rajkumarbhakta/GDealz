package com.rkbapps.gdealz.ui.tab.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SettingsRepository
) : ViewModel() {

    val isSystemTheme = repository.isSystemTheme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )
    val isDarkTheme = repository.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
    val selectedCountry = repository.selectedCountry.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "US"
    )
    val isNsfw = repository.isNsfw.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )



    val appVersion = BuildConfig.VERSION_NAME

    fun updateIsSystemTheme(value: Boolean) =
        viewModelScope.launch { repository.updateIsSystemTheme(value) }

    fun updateTheme(value: Boolean) = viewModelScope.launch { repository.updateTheme(value) }

    fun updateCountry(value: String) = viewModelScope.launch { repository.updateCountry(value) }

    fun updateNsfwContentAllowance(value: Boolean) =
        viewModelScope.launch { repository.updateNsfwContentAllowance(value) }




    fun sendNotification() {
        viewModelScope.launch {
            repository.sendNotification(context)
        }
    }

}