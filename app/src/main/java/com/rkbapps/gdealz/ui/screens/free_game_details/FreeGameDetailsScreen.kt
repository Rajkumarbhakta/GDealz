package com.rkbapps.gdealz.ui.screens.free_game_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.network.ApiConst.getFormattedDate
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.screens.free_game_details.composable.FreeGameDetailsBody


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameDetailsScreen(
    navController: NavHostController,
    viewModel: FreeGameDetailsViewModel = hiltViewModel()
) {

    val giveaway = remember { viewModel.giveaway }

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CommonTopBar(
                title = giveaway?.title ?: "",
                isNavigationBack = true
            ) {
                navController.navigateUp()
            }
        },
        bottomBar = {
            Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (giveaway?.isClaimed == true) {
                            viewModel.markGiveawayAsUnClaimed(giveaway)
                            navController.navigateUp()
                        } else {
                            giveaway?.let { viewModel.markGiveawayAsClaimed(it) }
                            giveaway?.openGiveawayUrl?.let {
                                uriHandler.openUri(it)
                            }
                        }
                    }
                ) {
                    Text(if (giveaway?.isClaimed == true) "Mark as un claimed" else "Grab Deal")
                }
            }
        }
    ) { innerPadding ->

        FreeGameDetailsBody(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding,), giveaway = giveaway)

    }
}