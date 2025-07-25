package com.rkbapps.gdealz.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.navigation.NavGraph
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.AppForegroundTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isSystemTheme =
            preferenceManager.getBooleanPreference(PreferenceManager.Companion.IS_USE_SYSTEM_THEME, true)
                .stateIn(
                    lifecycleScope,
                    SharingStarted.Companion.Lazily,
                    true
                )

        val isDarkTheme = preferenceManager.getBooleanPreference(PreferenceManager.Companion.IS_DARK_THEME, false)
            .stateIn(
                lifecycleScope,
                SharingStarted.Companion.Lazily,
                false
            )

        setContent {

            val isSystemTheme by isSystemTheme.collectAsStateWithLifecycle()
            val darkTheme by isDarkTheme.collectAsStateWithLifecycle()

            GDealzTheme(
                darkTheme = if (isSystemTheme) isSystemInDarkTheme() else darkTheme
            ) {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppForegroundTracker.setAppInForeground(true)
    }

    override fun onStop() {
        super.onStop()
        AppForegroundTracker.setAppInForeground(false)
    }

}