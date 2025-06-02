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
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.navigation.Routes

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {

    val isSuccess = remember {
        viewModel.isSuccess
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Splash Screen")

            if (isSuccess.value) {
                LaunchedEffect(key1 = Unit) {
                    navController.navigate(Routes.Main) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

}