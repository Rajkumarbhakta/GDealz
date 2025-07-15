package com.rkbapps.gdealz.ui.screens.game_info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.deal.Price
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.screens.dealslookup.getTotalReviews
import com.rkbapps.gdealz.ui.screens.steam_details.cheapshark.OverviewRowItems
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.StoreUtil



@Composable
fun GameInfoScreen(
    navController: NavHostController,
    viewModel: GameInfoViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current

    val gameInfo by viewModel.gameInfo.collectAsStateWithLifecycle()
    val gamePriceInfo by viewModel.gamePriceInfo.collectAsStateWithLifecycle()
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
                            SubcomposeAsyncImage (
                                model = gameInfo.data?.assets?.banner600,
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth,
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
                                /*item {
                                    OverviewRowItems(
                                        title = "DEAL",
                                        value = "${viewModel.deal.cut ?: 0}% OFF",
                                        subTitle = "${viewModel.deal.regular?.amount ?: 0}",
                                        subTitle1 = "${viewModel.deal.price?.amount ?: 0}",
                                        isSubTitleLineThrough = true
                                    )
                                }*/
                                item {
                                    OverviewRowItems(
                                        title = "RANK",
                                        value = getTotalReviews(gameInfo.data?.stats?.rank ?: 0),
                                        subTitle = "-",
                                        subTitle1 = "-"
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
                            /*Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                            }*/

                        }
                    }
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

@Composable
fun PriceHistoryCard(
    modifier: Modifier = Modifier,
    title: String,
    price: Price
) {
    Card {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(CurrencyAndCountryUtil.getCurrencyAndAmount(price))
        }
    }
}

@Composable
fun DealCard(modifier: Modifier = Modifier, deals: Deals, onClick: () -> Unit = {}) {

    val store = remember { StoreUtil.getStore(deals.shop?.id ?: 0) }
    val isFree = deals.cut == 100

    Card(
        onClick = onClick
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                SubcomposeAsyncImage (
                    model = store?.image,
                    contentDescription = store?.name,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                    error = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                        )
                    },
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                        )
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                deals.cut?.let {
                    Text(
                        text = (if (isFree) "Free" else "$it% OFF"),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        CurrencyAndCountryUtil.getCurrencyAndAmount(deals.regular),
                        style = MaterialTheme.typography.labelMedium.copy(textDecoration = TextDecoration.LineThrough),
                    )
                    Text(CurrencyAndCountryUtil.getCurrencyAndAmount(deals.price))
                }
                Text(deals.shop?.name?.toString() ?: "-")
            }
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.launch_link_open),
                    contentDescription = "open the deal"
                )
            }
        }
    }
}


// Utility functions to extract year, month, and day from a date string (YYYY-MM-DD)
fun getYear(dateString: String): String? {
    // Expecting dateString in format "YYYY-MM-DD"
    return dateString.split("-").getOrNull(0)
}

fun getMonths(dateString: String): String? {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val monthPart = dateString.split("-").getOrNull(1)
    return try {
        monthPart?.toIntOrNull()?.let {
            if (it in 1..12) months[it - 1] else null
        }
    } catch (e: Exception) {
        null
    }
}

fun getDate(dateString: String): String? {
    // Returns the day part as number or null if invalid
    return dateString.split("-").getOrNull(2)
}


