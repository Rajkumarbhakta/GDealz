package com.rkbapps.gdealz.ui.screens.image_preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(navController: NavHostController, imageUrl: String) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview") },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        null
                    ) }
                }
            )
        }
    ) {

        Box(modifier = Modifier.fillMaxSize().padding(it)){

            AsyncImage(
                model = imageUrl,
                contentDescription = "preview",
                modifier = Modifier.fillMaxSize().zoomable(rememberZoomState())
            )

        }


    }









}