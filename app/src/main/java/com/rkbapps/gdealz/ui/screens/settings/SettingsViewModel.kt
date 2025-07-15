package com.rkbapps.gdealz.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.util.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
): ViewModel() {

    val isSystemTheme = repository.isSystemTheme.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        true
    )
    val isDarkTheme = repository.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )
    val selectedCountry = repository.selectedCountry.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        "US"
    )
    val isNsfw = repository.isNsfw.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )


     fun updateIsSystemTheme(value: Boolean)=viewModelScope.launch {
        repository.updateIsSystemTheme(value)
    }
     fun updateTheme(value:Boolean) =viewModelScope.launch { repository.updateTheme(value) }

     fun updateCountry(value:String) =viewModelScope.launch { repository.updateCountry(value) }

     fun updateNsfwContentAllowance(value:Boolean) =viewModelScope.launch{ repository.updateNsfwContentAllowance(value) }

}