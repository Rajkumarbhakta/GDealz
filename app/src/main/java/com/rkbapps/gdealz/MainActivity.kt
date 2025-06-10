package com.rkbapps.gdealz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.navigation.NavGraph
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.AppForegroundTracker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GDealzTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController= rememberNavController()
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



