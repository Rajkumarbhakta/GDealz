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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SubcomposeAsyncImage(
                model = giveaway?.thumbnail,
                contentDescription = giveaway?.title,
                modifier = Modifier.fillMaxWidth(),
                error = {
                    Image(
                        painter = painterResource(R.drawable.console),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                loading = {
                    Image(
                        painter = painterResource(R.drawable.console),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(giveaway?.title ?: "", style = MaterialTheme.typography.titleLarge)

                Text(
                    "Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(giveaway?.description ?: "")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CommonCard(
                        modifier = Modifier.weight(1f),
                        title = "Platform",
                        subtitle = giveaway?.platforms ?: ""
                    )
                    CommonCard(
                        modifier = Modifier.weight(1f),
                        title = "Type",
                        subtitle = "${giveaway?.type}"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CommonCard(
                        modifier = Modifier.weight(1f),
                        title = "Published",
                        subtitle = getFormattedDate(giveaway?.publishedDate ?: "") ?: ""
                    )
                    CommonCard(
                        modifier = Modifier.weight(1f),
                        title = "Offer Ending",
                        subtitle = getFormattedDate(giveaway?.endDate ?: "") ?: ""
                    )
                }

                Text(
                    "Instruction",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(giveaway?.instructions ?: "")

            }
        }
    }
}