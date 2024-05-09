package com.rkbapps.gdealz.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.rkbapps.gdealz.ui.screens.MainScreen
import kotlinx.coroutines.delay

class SplashScreen:Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel:SplashScreenViewModel = hiltViewModel()

        val isSuccess = remember {
            viewModel.isSuccess
        }

//        LaunchedEffect(key1 = isSuccess.value) {
////            delay(3000)
//            navigator?.replace(MainScreen())
//        }

        Scaffold {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text(text = "Splash Screen")

                if (isSuccess.value){
                    LaunchedEffect(key1 = Unit) {
                        navigator?.replace(MainScreen())
                    }
                }
            }
        }



    }
}