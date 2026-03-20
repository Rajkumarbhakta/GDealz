package com.rkbapps.gdealz.ui.screens.image_preview

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ImagePreviewScreen(
    navController: NavHostController,
    imageUrl: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {

    Scaffold(
        topBar = {

            CommonTopBar(
                title = "Preview",
                isNavigationBack = true
            ) {
                navController.navigateUp()
            }
        }
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            rememberSharedContentState(key = imageUrl),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .zoomable(rememberZoomState())
                )
            }
        }
    }
}
