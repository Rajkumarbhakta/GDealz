package com.rkbapps.gdealz.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.navigation.Routes
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {

    val isSuccess = remember { viewModel.isSuccess }

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(100.dp)
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Text("v${viewModel.version}")
            Spacer(Modifier.weight(1f))
            Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                LoadingIndicator()
            }
            Spacer(Modifier.weight(1f))

            if (isSuccess.value) {
                LaunchedEffect(key1 = Unit) {
                    delay(500L)
                    navController.navigate(Routes.Main) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

}