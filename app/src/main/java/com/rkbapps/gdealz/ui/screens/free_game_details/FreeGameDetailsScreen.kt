package com.rkbapps.gdealz.ui.screens.free_game_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.screens.free_game_details.composable.FreeGameDetailsBody


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameDetailsScreen(
    navController: NavHostController,
    viewModel: FreeGameDetailsViewModel = hiltViewModel()
) {
    val giveaway by viewModel.giveaway.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CommonTopBar(
                title = giveaway.data?.title ?: "",
                isNavigationBack = true,
                actions = {
                    Button(onClick = {
                        viewModel.sendNotification()
                    }) {
                        Text("Send")
                    }
                }
            ) {
                navController.navigateUp()
            }
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(BottomAppBarDefaults.windowInsets.asPaddingValues())
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    giveaway.data?.let { giveaway ->
                        if (giveaway.isClaimed) {
                            viewModel.markGiveawayAsUnClaimed(giveaway)
                            navController.navigateUp()
                        } else {
                            viewModel.markGiveawayAsClaimed(giveaway)
                            giveaway.openGiveawayUrl.let {
                                uriHandler.openUri(it)
                            }
                        }
                    }
                }
            ) {
                Text(if (giveaway.data?.isClaimed == true) stringResource(R.string.mark_as_unclaimed) else stringResource(R.string.grab_deal))
            }
        }
    ) { innerPadding ->
        when{
            giveaway.data!=null ->{
                FreeGameDetailsBody(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding,), giveaway = giveaway.data
                )
            }
            else ->{
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center){
                    if (giveaway.error!=null){
                        ErrorScreen(message = giveaway.error?:"Something went wrong.")
                    }else{
                        CircularWavyProgressIndicator()
                    }
                }
            }
        }
    }
}