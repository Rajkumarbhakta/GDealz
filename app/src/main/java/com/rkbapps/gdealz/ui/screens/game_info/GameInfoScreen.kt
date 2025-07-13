package com.rkbapps.gdealz.ui.screens.game_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.screens.dealslookup.getTotalReviews
import com.rkbapps.gdealz.ui.screens.steam_details.OverviewRowItems
import com.rkbapps.gdealz.ui.screens.steam_details.getDate
import com.rkbapps.gdealz.ui.screens.steam_details.getMonths
import com.rkbapps.gdealz.ui.screens.steam_details.getYear

@Composable
fun GameInfoScreen(
    navController: NavHostController,
    viewModel: GameInfoViewModel = hiltViewModel()
) {

    val gameInfo by viewModel.gameInfo.collectAsStateWithLifecycle()
    val favStatus by viewModel.dealFavStatus.collectAsStateWithLifecycle()
    val isFav by remember { viewModel.isFavDeal }


    Scaffold(
        topBar = {
            CommonTopBar(
                title = viewModel.title ?: stringResource(id = R.string.app_name),
                isNavigationBack = true,
                actions = {
                    FilledIconButton(
                        onClick = {
                            gameInfo.data?.let { viewModel.toggleFavDeal(it) }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            "is fav"
                        )
                    }
                }
            ) {
                navController.navigateUp()
            }
        },
    ) { innerPadding ->
        when {
            gameInfo.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            gameInfo.error != null -> {
                ErrorScreen(gameInfo.error ?: "An error occurred.")
            }

            gameInfo.data != null -> {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = gameInfo.data?.assets?.banner600,
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                OutlinedCard(
                                    shape = RoundedCornerShape(100.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.2f
                                        ),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = (if (gameInfo.data?.mature == true) "18+" else "ALL"),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    alignment = Alignment.CenterHorizontally
                                )
                            ) {
                                item {
                                    if (!gameInfo.data?.reviews.isNullOrEmpty()) {
                                        OverviewRowItems(
                                            title = "REVIEWS",
                                            value = getTotalReviews(
                                                count = gameInfo.data?.reviews?.first()?.count ?: 0
                                            ),
                                            subTitle = "${gameInfo.data?.reviews?.first()?.score ?: 0}%",
                                            subTitle1 = gameInfo.data?.reviews?.first()?.source
                                                ?: "Unknown"
                                        )
                                    }
                                }
                                item {
                                    OverviewRowItems(
                                        title = "RANK",
                                        value = getTotalReviews(gameInfo.data?.stats?.rank ?: 0),
                                        subTitle = "",
                                        subTitle1 = ""
                                    )
                                }
                                item {
                                    if (gameInfo.data?.earlyAccess == true) {
                                        OverviewRowItems(
                                            title = "RELEASED",
                                            value = "Coming",
                                            subTitle = "Soon",
                                            subTitle1 = ""
                                        )
                                    } else {
                                        OverviewRowItems(
                                            title = "RELEASED",
                                            value = getYear(gameInfo.data?.releaseDate ?: "")
                                                ?: "-",
                                            subTitle = getMonths(gameInfo.data?.releaseDate ?: "")
                                                ?: "-",
                                            subTitle1 = getDate(gameInfo.data?.releaseDate ?: "")
                                                ?: "-"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            CommonCard(
                                modifier = Modifier.weight(1f),
                                title = "Developer",
                                subtitle = try {
                                    gameInfo.data?.developers?.first()?.name ?: "-"
                                } catch (_: Exception) {
                                    "-"
                                },
                            )
                            CommonCard(
                                modifier = Modifier.weight(1f),
                                title = "Publisher",
                                subtitle = try {
                                    gameInfo.data?.publishers?.first()?.name ?: "-"
                                } catch (_: Exception) {
                                    "-"
                                }
                            )
                        }
                    }
                    item {
                        Text(
                            "Features",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {

                            gameInfo.data?.tags?.forEach { tag ->
                                OutlinedCard {
                                    Text(
                                        tag,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }


}