package com.rkbapps.gdealz.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class HomeScreen:Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.current

        Scaffold {
            Column(modifier = Modifier.fillMaxSize().padding(it)) {

            }
        }

    }
}