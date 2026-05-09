package com.rkbapps.gdealz.ui.screens.steam_details.cheapshark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.network.ApiConst.IMAGE_URL
import com.rkbapps.gdealz.ui.composables.CommonCard
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.screens.dealslookup.getTotalReviews
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.calculatePercentage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SteamDetailsPage(
    navController: NavHostController,
    viewModel: SteamDetailsViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val months = remember { context.resources.getStringArray(R.array.months).toList() }
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
                            stringResource(R.string.favorite_icon)
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
                            Text(text = stringResource(R.string.grab_the_deal))
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.launch_link_open),
                                contentDescription = stringResource(R.string.open_deal),
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
                            Text(text = stringResource(R.string.go_official_website))
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.launch_link_open),
                                contentDescription = stringResource(R.string.open_deal),
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
                                contentDescription = stringResource(R.string.banner),
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
                                            " ${steamGameData.data.data?.metacritic?.score ?: stringResource(R.string.not_available)}",
                                        )
                                        Text(
                                            stringResource(R.string.meta_critic),
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
                                        stringResource(R.string.age_limit_format, steamGameData.data.data?.requiredAge ?: 0),
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
                                        title = stringResource(R.string.rating_and_review),
                                        value = getTotalReviews(
                                            count = dealsData.value.data?.gameInfo?.steamRatingCount
                                                ?: "0"
                                        ),
                                        subTitle = stringResource(R.string.score_percent_format, dealsData.value.data?.gameInfo?.steamRatingPercent ?: 0),
                                        subTitle1 = dealsData.value.data?.gameInfo?.steamRatingText
                                            ?: stringResource(R.string.unknown)
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
                                        title = stringResource(R.string.deal_label),
                                        value = stringResource(R.string.discount_percent_format, percentage, stringResource(R.string.off)),
                                        subTitle = stringResource(R.string.price_format, CurrencyAndCountryUtil.formatPrice(dealsData.value.data?.gameInfo?.retailPrice)),
                                        subTitle1 = if (isFree) stringResource(R.string.free) else stringResource(R.string.price_format, CurrencyAndCountryUtil.formatPrice(dealsData.value.data?.gameInfo?.salePrice)),
                                        isSubTitleLineThrough = true
                                    )
                                }
                                item {
                                    OverviewRowItems(
                                        title = stringResource(R.string.genre).uppercase(),
                                        value = steamGameData.data.data?.genres?.firstOrNull()?.description ?: stringResource(R.string.not_available),
                                        subTitle = steamGameData.data.data?.genres?.getOrNull(1)?.description ?: stringResource(R.string.not_available),
                                        subTitle1 = ""
                                    )
                                }
                                item {
                                    if (steamGameData.data.data?.releaseDate?.comingSoon == true) {
                                        OverviewRowItems(
                                            title = stringResource(R.string.released).uppercase(),
                                            value = stringResource(R.string.coming),
                                            subTitle = stringResource(R.string.soon),
                                            subTitle1 = ""
                                        )
                                    } else {
                                        OverviewRowItems(
                                            title = stringResource(R.string.released).uppercase(),
                                            value = getYear(
                                                steamGameData.data.data?.releaseDate?.date ?: ""
                                            ) ?: stringResource(R.string.not_available),
                                            subTitle = getMonths(
                                                steamGameData.data.data?.releaseDate?.date ?: "",
                                                months
                                            ) ?: stringResource(R.string.not_available),
                                            subTitle1 = getDate(
                                                steamGameData.data.data?.releaseDate?.date ?: ""
                                            ) ?: stringResource(R.string.not_available)
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
                                    title = stringResource(R.string.developer),
                                    subtitle = steamGameData.data.data?.developers?.firstOrNull() ?: stringResource(R.string.not_available),
                                )
                                CommonCard(
                                    modifier = Modifier,
                                    title = stringResource(R.string.publisher),
                                    subtitle = steamGameData.data.data?.publishers?.firstOrNull() ?: stringResource(R.string.not_available)
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
                                            text = stringResource(R.string.available_on),
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(modifier = Modifier.size(50.dp)) {
                                            SubcomposeAsyncImage(
                                                model = IMAGE_URL + storeData.value?.banner,
                                                contentDescription = stringResource(R.string.store_logo),
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
                                            text = storeData.value?.storeName ?: stringResource(R.string.unknown),
                                            textAlign = TextAlign.Center
                                        )


                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            stringResource(R.string.about_this_game),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Text(
                            steamGameData.data.data?.shortDescription ?: stringResource(R.string.nothing_here),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    item {
                        Text(
                            stringResource(R.string.screenshots),
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
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable{
                                            it.pathFull?.let { imageUri->
                                                navController.navigate(Routes.ImagePreview(imageUri))
                                            }
                                        }
                                    ,
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
                            stringResource(R.string.features),
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
                            stringResource(R.string.system_requirements),
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

private fun getMonths(date: String, months: List<String>): String? {
    val englishMonths = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    try {
        val parts = date.split(" ", ", ")
        val monthPart = parts[1]
        val index = englishMonths.indexOf(monthPart)
        return if (index != -1) months[index] else monthPart
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

