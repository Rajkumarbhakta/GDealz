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
    val name: String,
    val code: String,
    val displayLanguage: String
)

val appLanguages = listOf(
    Language("english","en", "English"), // default language
    Language("russian","ru", "Русский"),
    Language("portuguese", "pt", "Português"),
    Language("spanish", "es", "Español"),
    Language("german", "de", "Deutsch"),
    Language("french", "fr", "Français"),
    Language("japanese", "ja", "日本語"),
    Language("korean", "ko", "한국어"),
    Language("hindi","hi", "हिन्दी"),
    Language("bengali", "bn", "বাংলা"),
    Language("schinese", "zh-CN", "简体中文"),
    Language("tchinese", "zh-TW", "繁體中文"),

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
        return try {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.getSystemService(LocaleManager::class.java)
                    ?.applicationLocales
                    ?.get(0)
            } else {
                AppCompatDelegate.getApplicationLocales().get(0)
            }
            val tag = locale?.toLanguageTag()
            if (tag != null && appLanguages.any { it.code.equals(tag, ignoreCase = true) }) {
                tag
            } else {
                locale?.language ?: getDefaultLanguageCode()
            }
        } catch (e: Exception) {
            getDefaultLanguageCode()
        }
    }

    fun getLanguageFromCode(languageCode: String): Language {
        return appLanguages.find { it.code == languageCode } ?: appLanguages.first()
    }

    private fun getDefaultLanguageCode(): String {
        return  appLanguages.first().code
    }
}