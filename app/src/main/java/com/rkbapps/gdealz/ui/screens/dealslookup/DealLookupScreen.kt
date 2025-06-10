package com.rkbapps.gdealz.ui.screens.dealslookup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.network.ApiConst.IMAGE_URL
import com.rkbapps.gdealz.network.ApiConst.getFormattedDate
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.theme.darkGreen
import com.rkbapps.gdealz.ui.theme.normalTextColor
import com.rkbapps.gdealz.util.calculatePercentage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealLookupScreen(
    navController: NavHostController,
    viewModel: DealLookupViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current
    val dealsData = viewModel.dealData.collectAsStateWithLifecycle()
    val storeData = viewModel.storeData.collectAsStateWithLifecycle()
    val favStatus = viewModel.dealFavStatus.collectAsStateWithLifecycle()
    val isFav = remember { viewModel.isFavDeal }


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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.redirectionUrl?.let { url ->
                                uriHandler.openUri(url)
                            }
                        }
                    ) {
                        Text(text = "Garb the deal")
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            when {
                dealsData.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                dealsData.value.error != null -> {
                    ErrorScreen(dealsData.value.error ?: "Something went wrong!")
                }

                dealsData.value.data != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(10.dp)
                                    ).clip(RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = dealsData.value.data?.gameInfo?.thumb,
                                    contentDescription = "game thumb",
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Card(
                                modifier = Modifier.weight(1f).height(80.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = viewModel.title ?: "",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    text = "Special Deal",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally)
                                ) {
                                    val percentage = remember { calculatePercentage(
                                        dealsData.value.data?.gameInfo?.retailPrice ?: "",
                                        dealsData.value.data?.gameInfo?.salePrice ?: ""
                                    ) }
                                    val isFree = remember { (dealsData.value.data?.gameInfo?.salePrice ?: "").contains("0.00") }

                                    Text(
                                        text = if (isFree) "Free" else "${percentage}% OFF",
                                        color = if (isFree) darkGreen else
                                            MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(
                                                color =
                                                    if (isFree) darkGreen.copy(alpha = 0.2f) else
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            ).padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                    Text(
                                        text = "$${dealsData.value.data?.gameInfo?.retailPrice}",
                                        color = normalTextColor,
                                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                                    )

                                    Text(
                                        text = if (isFree) "Free" else "$${dealsData.value.data?.gameInfo?.salePrice}",
                                        color = if (isFree) darkGreen else MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                        }

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    text = "Ratting & Review",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly

                                ) {
                                    ReviewItems(
                                        title = "Meta Critic", value = " ${
                                            dealsData.value.data?.gameInfo?.metaCriticScore ?: 0
                                        }"
                                    )

                                    ReviewItems(
                                        title = "Total Rating",
                                        value = getTotalReviews(
                                            count = dealsData.value.data?.gameInfo?.steamRatingCount
                                                ?: "0"
                                        )
                                    )

                                    ReviewItems(
                                        title = "Rate", value = "${
                                            dealsData.value.data?.gameInfo?.steamRatingPercent ?: 0
                                        }%"
                                    )

                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Card {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "Overall Ratting",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = dealsData.value.data?.gameInfo?.steamRatingText
                                                ?: "Unknown",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleSmall,
                                        )

                                    }


                                }

                            }

                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            if (storeData.value != null) {
                                OutlinedCard(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Available On",
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(modifier = Modifier.size(50.dp)) {
                                            AsyncImage(
                                                model = IMAGE_URL + storeData.value?.banner,
                                                contentDescription = "store logo",
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(8.dp),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = storeData.value?.storeName ?: "Unknown",
                                            textAlign = TextAlign.Center
                                        )


                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            val releaseDate = remember { getFormattedDate(dealsData.value.data?.gameInfo?.releaseDate) }

                            CommonCard(
                                modifier = Modifier
                                    .height(130.dp)
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                title = "Release Info",
                                subtitle = releaseDate ?: "Unknown"
                            )

                        }
                    }
                }
            }
        }
    }
}

private fun getTotalReviews(count: String): String {
    return try {
        if (count.toLong() > 1000) {
            "${count.toLong() / 1000}K"
        } else {
            count
        }
    } catch (_: Exception) {
        count
    }
}


@Composable
private fun RowScope.ReviewItems(title: String, value: Any) {
    Card(
        modifier = Modifier.weight(1f).padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .height(50.dp)
                    .width(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


        }
    }


}
