package com.rkbapps.gdealz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay

class SplashScreen:Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        LaunchedEffect(key1 = Unit) {
            delay(3000)
            navigator?.replace(MainScreen())
        }


        Scaffold {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                Text(text = "Splash Screen")


            }
        }



    }
}