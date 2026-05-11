package com.rkbapps.gdealz.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.db.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
): ViewModel() {
    val isSystemTheme = preferenceManager.getBooleanPreference(PreferenceManager.IS_USE_SYSTEM_THEME, true)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )

    val isDarkTheme = preferenceManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME, false)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    val isDynamicColor = preferenceManager.getBooleanPreference(PreferenceManager.IS_DYNAMIC_THEME, true)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )
}