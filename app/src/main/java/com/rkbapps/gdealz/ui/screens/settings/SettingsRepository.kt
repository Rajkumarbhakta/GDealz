package com.rkbapps.gdealz.ui.screens.settings

import com.rkbapps.gdealz.db.PreferenceManager
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val prefManager: PreferenceManager
) {

    val isSystemTheme = prefManager.getBooleanPreference(PreferenceManager.IS_USE_SYSTEM_THEME,true)
    val isDarkTheme = prefManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME)
    val selectedCountry = prefManager.getStringPreference(PreferenceManager.SELECTED_COUNTRY,)
    val isNsfw = prefManager.getBooleanPreference(PreferenceManager.IS_NSFW_ALLOWED)


    suspend fun updateIsSystemTheme(value: Boolean)=prefManager.saveBooleanPreference(PreferenceManager.IS_USE_SYSTEM_THEME,value)
    suspend fun updateTheme(value:Boolean) = prefManager.saveBooleanPreference(PreferenceManager.IS_DARK_THEME,value)

    suspend fun updateCountry(value:String) = prefManager.saveStringPreference(PreferenceManager.SELECTED_COUNTRY,value)

    suspend fun updateNsfwContentAllowance(value:Boolean) = prefManager.saveBooleanPreference(PreferenceManager.IS_NSFW_ALLOWED,value)









}