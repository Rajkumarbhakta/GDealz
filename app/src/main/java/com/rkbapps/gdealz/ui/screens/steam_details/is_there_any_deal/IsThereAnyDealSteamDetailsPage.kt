package com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.network.ApiConst.IMAGE_URL
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.screens.dealslookup.getTotalReviews
import com.rkbapps.gdealz.ui.screens.game_info.DealCard
import com.rkbapps.gdealz.ui.screens.game_info.PriceHistoryCard
import com.rkbapps.gdealz.ui.screens.steam_details.cheapshark.OverviewRowItems
import com.rkbapps.gdealz.util.calculatePercentage

@Composable
fun IsThereAnyDealSteamDetailsPage(navController: NavHostController, viewModel: IsThereAnyDealSteamViewModel = hiltViewModel()) {

    val uriHandler = LocalUriHandler.current

    val gameData by viewModel.gameInfo.collectAsStateWithLifecycle()
    val gamePriceInfo by viewModel.gamePriceInfo.collectAsStateWithLifecycle()
    val steamGameData by viewModel.steamGameData.collectAsStateWithLifecycle()

    val favStatus by viewModel.dealFavStatus.collectAsStateWithLifecycle()
    val isFav = remember { viewModel.isFavDeal }




    Scaffold(
        topBar = {
            CommonTopBar(
                title = viewModel.title ?: stringResource(id = R.string.app_name),
                isNavigationBack = true,
                actions = {
                    FilledIconButton(
                        onClick = {
                            gameData.data?.let { viewModel.toggleFavDeal(it) }
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
            AnimatedVisibility(steamGameData.data != null) {
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
                            steamGameData.data?.data?.website?.let {
                                uriHandler.openUri(it)
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.weight(1f))
                            Text(text = "Go official website")
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.launch_link_open),
                                contentDescription = "launch link open",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when {
            steamGameData.isLoading || gameData.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            steamGameData.error != null && gameData.error!=null -> {
                navController.navigate(
                    Routes.GameInfo(
                        gameId = viewModel.gameId,
                        title = viewModel.title
                    )
                ) {
                    navController.popBackStack()
                }
            }
            steamGameData.data != null -> {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // banner
                    item {
                        Box(Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = steamGameData.data?.data?.headerImage,
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
                                    ),
                                    onClick = {
                                        uriHandler.openUri(
                                            steamGameData.data?.data?.metacritic?.url
                                                ?: "https://www.metacritic.com/"
                                        )
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            " ${steamGameData.data?.data?.metacritic?.score ?: "-"}",
                                        )
                                        Text(
                                            "META CRITIC",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
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
                                        " ${steamGameData.data?.data?.requiredAge}+",
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                    //overview cards
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
                                    if (!gameData.data?.reviews.isNullOrEmpty()) {
                                        OverviewRowItems(
                                            title = "REVIEWS",
                                            value = getTotalReviews(
                                                count = gameData.data?.reviews?.first()?.count ?: 0
                                            ),
                                            subTitle = "${gameData.data?.reviews?.first()?.score ?: 0}%",
                                            subTitle1 = gameData.data?.reviews?.first()?.source
                                                ?: "-"
                                        )
                                    }
                                }

                                item {
                                    OverviewRowItems(
                                        title = "GENRE",
                                        value = try {
                                            steamGameData.data?.data?.genres[0]?.description ?: "-"
                                        } catch (_: Exception) {
                                            "-"
                                        },
                                        subTitle = try {
                                            steamGameData.data?.data?.genres[1]?.description ?: "-"
                                        } catch (_: Exception) {
                                            "-"
                                        },
                                        subTitle1 = ""
                                    )
                                }
                                item {
                                    if (steamGameData.data?.data?.releaseDate?.comingSoon == true) {
                                        OverviewRowItems(
                                            title = "RELEASED",
                                            value = "Coming",
                                            subTitle = "Soon",
                                            subTitle1 = ""
                                        )
                                    } else {
                                        OverviewRowItems(
                                            title = "RELEASED",
                                            value = getYear(
                                                steamGameData.data?.data?.releaseDate?.date ?: ""
                                            ) ?: "-",
                                            subTitle = getMonths(
                                                steamGameData.data?.data?.releaseDate?.date ?: ""
                                            ) ?: "-",
                                            subTitle1 = getDate(
                                                steamGameData.data?.data?.releaseDate?.date ?: ""
                                            ) ?: "-"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // developer and publisher
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
                                    steamGameData.data?.data?.developers?.first() ?: "-"
                                } catch (_: Exception) {
                                    "-"
                                },
                            )

                            CommonCard(
                                modifier = Modifier.weight(1f),
                                title = "Publisher",
                                subtitle = try {
                                    steamGameData.data?.data?.publishers?.first() ?: "-"
                                } catch (_: Exception) {
                                    "-"
                                }
                            )

                            /*Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {


                            }*/
                        }
                    }
                    //about the game
                    item {
                        Text(
                            "About this Game",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Text(
                            steamGameData.data?.data?.shortDescription ?: "Nothing available",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    // screenshots
                    item {
                        Text(
                            "Screenshots",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Spacer(Modifier.width(10.dp))
                            }
                            items(steamGameData.data?.data?.screenshots ?: emptyList()) {
                                AsyncImage(
                                    model = it.pathFull,
                                    contentDescription = "",
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            }
                            item {
                                Spacer(Modifier.width(10.dp))
                            }
                        }
                    }
                    //price history
                    item {
                        Text(
                            "Price History",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        when {
                            gamePriceInfo.isLoading -> {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }

                            gamePriceInfo.error != null -> {
                                Text(
                                    "${gamePriceInfo.error}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }

                            gamePriceInfo.data != null -> {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    gamePriceInfo.data?.historyLow?.all?.let {
                                        PriceHistoryCard(
                                            title = "Lowest in history",
                                            price = it
                                        )
                                    }
                                    gamePriceInfo.data?.historyLow?.y1?.let {
                                        PriceHistoryCard(
                                            title = "Lowest in 1 year",
                                            price = it
                                        )
                                    }
                                    gamePriceInfo.data?.historyLow?.m3?.let {
                                        PriceHistoryCard(
                                            title = "Lowest in 3 months",
                                            price = it
                                        )
                                    }
                                }
                            }

                        }
                    }
                    //Deals
                    item {
                        Text(
                            "Deals",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        when {
                            gamePriceInfo.isLoading -> {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }

                            gamePriceInfo.error != null -> {
                                Text(
                                    "${gamePriceInfo.error}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }

                            gamePriceInfo.data != null -> {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    gamePriceInfo.data?.deals?.forEach { deals ->
                                        DealCard(deals = deals) {
                                            deals.url?.let {
                                                uriHandler.openUri(it)
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    //Features
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
                            steamGameData.data?.data?.categories?.let {
                                it.forEach { category ->
                                    OutlinedCard {
                                        Text(
                                            category.description ?: "",
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // system requirement
                    item {
                        Text(
                            "System Requirements",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            steamGameData.data?.data?.pcRequirements?.minimum?.let {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        AnnotatedString.fromHtml(it),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            steamGameData.data?.data?.pcRequirements?.recommended?.let {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        AnnotatedString.fromHtml(it),
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




private fun getDate(date: String): String? {
    try {
        val parts = date.split(" ", ", ")
        return parts[0]
    } catch (_: Exception) {
        return null
    }
}

private fun getMonths(date: String): String? {
    try {
        val parts = date.split(" ", ", ")
        return parts[1]
    } catch (_: Exception) {
        return null
    }
}

private fun getYear(date: String): String? {
    try {
        val parts = date.split(" ", ", ")
        return parts[2]
    } catch (_: Exception) {
        return null
    }
}