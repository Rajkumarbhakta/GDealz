package com.rkbapps.gdealz.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

data class Language(
    val code: String,
    val displayLanguage: String
)

val appLanguages = listOf(
    Language("en", "English"), // default language
    Language("ru", "Русский"),
    Language("hi", "हिन्दी")
)

object AppLocaleManager {

    fun changeLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    fun getLanguageCode(context: Context,): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales
                ?.get(0)
        } else {
            AppCompatDelegate.getApplicationLocales().get(0)
        }
        return locale?.language ?: getDefaultLanguageCode()
    }

    private fun getDefaultLanguageCode(): String {
        return  appLanguages.first().code
    }
}