package com.rkbapps.gdealz.ui.screens.steam_details.cheapshark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.network.ApiConst.IMAGE_URL
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.screens.dealslookup.getTotalReviews
import com.rkbapps.gdealz.util.calculatePercentage

@Composable
fun SteamDetailsPage(
    navController: NavHostController,
    viewModel: SteamDetailsViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current
    val dealsData = viewModel.dealData.collectAsStateWithLifecycle()
    val storeData = viewModel.storeData.collectAsStateWithLifecycle()
    val favStatus = viewModel.dealFavStatus.collectAsStateWithLifecycle()
    val isFav = remember { viewModel.isFavDeal }
    val steamGameData = viewModel.steamGameData.collectAsStateWithLifecycle().value


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
            steamGameData.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            steamGameData.error != null -> {
                navController.navigate(
                    Routes.DealsLookup(
                        dealId = viewModel.dealLookup.dealId,
                        title = viewModel.dealLookup.title
                    )
                ) {
                    navController.popBackStack()
                }
                //ErrorScreen(steamGameData.error)
            }

            steamGameData.data != null -> {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(Modifier.fillMaxWidth()) {
                            SubcomposeAsyncImage(
                                model = steamGameData.data.data?.headerImage,
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
                                    ),
                                    onClick = {
                                        uriHandler.openUri(
                                            steamGameData.data.data?.metacritic?.url
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
                                            " ${steamGameData.data.data?.metacritic?.score ?: "-"}",
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
                                        " ${steamGameData.data.data?.requiredAge}+",
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
                                    OverviewRowItems(
                                        title = "REVIEWS",
                                        value = getTotalReviews(
                                            count = dealsData.value.data?.gameInfo?.steamRatingCount
                                                ?: "0"
                                        ),
                                        subTitle = "${dealsData.value.data?.gameInfo?.steamRatingPercent ?: 0}%",
                                        subTitle1 = dealsData.value.data?.gameInfo?.steamRatingText
                                            ?: "Unknown"
                                    )
                                }
                                item {
                                    val isFree = remember(
                                        dealsData.value.data?.gameInfo?.salePrice
                                    ) {
                                        (dealsData.value.data?.gameInfo?.salePrice
                                            ?: "").contains("0.00")
                                    }

                                    val percentage =
                                        remember(dealsData.value.data?.gameInfo?.retailPrice) {
                                            calculatePercentage(
                                                dealsData.value.data?.gameInfo?.retailPrice ?: "",
                                                dealsData.value.data?.gameInfo?.salePrice ?: ""
                                            )
                                        }

                                    OverviewRowItems(
                                        title = "DEAL",
                                        value = "${percentage}% OFF",
                                        subTitle = "$${dealsData.value.data?.gameInfo?.retailPrice}",
                                        subTitle1 = if (isFree) "Free" else "$${dealsData.value.data?.gameInfo?.salePrice}",
                                        isSubTitleLineThrough = true
                                    )
                                }
                                item {
                                    OverviewRowItems(
                                        title = "GENRE",
                                        value = try {
                                            steamGameData.data.data?.genres[0]?.description ?: "-"
                                        } catch (_: Exception) {
                                            "-"
                                        },
                                        subTitle = try {
                                            steamGameData.data.data?.genres[1]?.description ?: "-"
                                        } catch (_: Exception) {
                                            "-"
                                        },
                                        subTitle1 = ""
                                    )
                                }
                                item {
                                    if (steamGameData.data.data?.releaseDate?.comingSoon == true) {
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
                                                steamGameData.data.data?.releaseDate?.date ?: ""
                                            ) ?: "-",
                                            subTitle = getMonths(
                                                steamGameData.data.data?.releaseDate?.date ?: ""
                                            ) ?: "-",
                                            subTitle1 = getDate(
                                                steamGameData.data.data?.releaseDate?.date ?: ""
                                            ) ?: "-"
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
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CommonCard(
                                    modifier = Modifier,
                                    title = "Developer",
                                    subtitle = try {
                                        steamGameData.data.data?.developers?.first() ?: "-"
                                    } catch (_: Exception) {
                                        "-"
                                    },
                                )
                                CommonCard(
                                    modifier = Modifier,
                                    title = "Publisher",
                                    subtitle = try {
                                        steamGameData.data.data?.publishers?.first() ?: "-"
                                    } catch (_: Exception) {
                                        "-"
                                    }
                                )
                            }
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
                                            SubcomposeAsyncImage(
                                                model = IMAGE_URL + storeData.value?.banner,
                                                contentDescription = "store logo",
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(8.dp),
                                                contentScale = ContentScale.Fit,
                                                error = {
                                                    Image(
                                                        painter = painterResource(R.drawable.console),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(50.dp),
                                                    )
                                                },
                                                loading = {
                                                    Image(
                                                        painter = painterResource(R.drawable.console),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(50.dp),
                                                    )
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = storeData.value?.storeName ?: "Unknown",
                                            textAlign = TextAlign.Center
                                        )


                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "About this Game",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Text(
                            steamGameData.data.data?.shortDescription ?: "Nothing available",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

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
                            items(steamGameData.data.data?.screenshots ?: emptyList()) {
                                SubcomposeAsyncImage(
                                    model = it.pathFull,
                                    contentDescription = "",
                                    contentScale = ContentScale.FillHeight,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    error = {
                                        Image(
                                            painter = painterResource(R.drawable.console),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp),
                                        )
                                    },
                                    loading = {
                                        Image(
                                            painter = painterResource(R.drawable.console),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp),
                                        )
                                    }
                                )
                            }
                            item {
                                Spacer(Modifier.width(10.dp))
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
                            steamGameData.data.data?.categories?.let {
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
                            steamGameData.data.data?.pcRequirements?.minimum?.let {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        AnnotatedString.fromHtml(it),
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            steamGameData.data.data?.pcRequirements?.recommended?.let {
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


@Composable
fun OverviewRowItems(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subTitle: String,
    subTitle1: String,
    isSubTitleLineThrough: Boolean = false
) {
    OutlinedCard {
        Column(
            modifier = modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, maxLines = 1)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                subTitle,
                style = MaterialTheme.typography.labelMedium.copy(
                    textDecoration = if (isSubTitleLineThrough) TextDecoration.LineThrough else TextDecoration.None
                ),
            )
            Text(subTitle1, style = MaterialTheme.typography.labelSmall, minLines = 1)
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

