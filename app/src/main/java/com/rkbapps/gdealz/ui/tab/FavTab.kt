package com.rkbapps.gdealz.ui.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.ui.composables.CommonTopBar

@Composable
fun FavTab(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CommonTopBar("Fav")
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {

            Text(text = "Fav")
        }
    }
}