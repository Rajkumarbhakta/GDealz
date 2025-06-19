package com.rkbapps.gdealz.ui.screens.steam_details

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen

@Composable
fun SteamDetailsPage(navController: NavHostController,viewModel: SteamDetailsViewModel = hiltViewModel()) {

    val uriHandler = LocalUriHandler.current
    val dealsData = viewModel.dealData.collectAsStateWithLifecycle()
    val storeData = viewModel.storeData.collectAsStateWithLifecycle()
    val favStatus = viewModel.dealFavStatus.collectAsStateWithLifecycle()
    val isFav = remember { viewModel.isFavDeal }
    val steamGameData  =  viewModel.steamGameData.collectAsStateWithLifecycle().value


    Scaffold(
        topBar = {
            CommonTopBar(
                title = viewModel.title ?: stringResource(id = R.string.app_name),
                isNavigationBack = true,
                actions = {
                    FilledIconButton(
                        onClick = {
                            dealsData.value.data?.let { viewModel.toggleFavDeal(it) }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isFav.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            "back"
                        )
                    }
                }
            ) {
                navController.navigateUp()
            }
        },
        bottomBar = {
            AnimatedVisibility(dealsData.value.data != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.redirectionUrl?.let { url ->
                                uriHandler.openUri(url)
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.weight(1f))
                            Text(text = "Grab the deal")
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.launch_link_open),
                                contentDescription = "launch link open",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
//                            navController.navigate(
//                                Routes.DealsLookup(
//                                    title = viewModel.title,
//                                    dealId = dealsData.value.data.cheapestPrice.price,
//                                    isCheapest = true
//                                )
//                            )
                        }
                    ) {
                        Text(text = "Find Cheapest Deal")
                    }

                }
            }
        }
    ) { innerPadding->
        when{
            steamGameData.isLoading==true->{
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }
            steamGameData.error!=null -> {
//                navController.navigate(
//                    Routes.DealsLookup(
//                        dealId = viewModel.dealLookup.dealId,
//                        title = viewModel.dealLookup.title
//                    )
//                ){
//                    navController.popBackStack()
//                }
                ErrorScreen(steamGameData.error)
            }
            steamGameData.data!=null->{
                LazyColumn(
                    contentPadding = innerPadding
                ) {
                    item {
                        Box(Modifier.fillMaxWidth(),){
                            AsyncImage(
                                model = steamGameData.data.data?.headerImage,
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            factory = {context->
                                WebView(context).apply {
                                    loadData(
                                        steamGameData.data.data?.detailedDescription!!,
                                        "text/html",
                                        "UTF-8"
                                    )
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                }
                            }
                        )

                    }




                }
            }
        }



    }
    
}