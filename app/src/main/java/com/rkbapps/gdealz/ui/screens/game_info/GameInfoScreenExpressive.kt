package com.rkbapps.gdealz.ui.screens.game_info

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.BestPriceSection
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.ExpandableTags
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.FavoriteButton
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.Hero
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.HeroButton
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.PageSidePadding
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.PriceHistorySection
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.QuickFact
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.QuickFactsRow
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.StorePrices
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.HeroStatusBarIcons
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.StoresItemIndex
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.heroTopBarColors
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.heroTopBarHeightPx
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.isPastHero
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.getTotalReviews
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/** The API sends "YYYY-MM-DD"; anything else is shown as it came. */
private fun formatReleaseDate(date: String): String = try {
    SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
        ?.let { SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(it) } ?: date
} catch (_: Exception) {
    date
}

/** For games without Steam details, so everything here comes from IsThereAnyDeal alone. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameInfoScreenExpressive(
    navController: NavHostController,
    viewModel: GameInfoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val gameInfo by viewModel.gameInfo.collectAsStateWithLifecycle()
    val gamePriceInfo by viewModel.gamePriceInfo.collectAsStateWithLifecycle()
    val priceHistory by viewModel.priceHistory.collectAsStateWithLifecycle()
    val isFav by viewModel.isFavDeal

    val game = gameInfo.data
    if (game == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (gameInfo.error != null) {
                ErrorScreen(gameInfo.error ?: stringResource(R.string.error_occurred))
                HeroButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(12.dp)
                )
            } else {
                ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
            }
        }
        return
    }

    val openUrl: (String?) -> Unit = { url ->
        try {
            url?.let { uriHandler.openUri(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, R.string.unable_to_open_website, Toast.LENGTH_SHORT).show()
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val prices = gamePriceInfo.data
    val bestDeal = remember(prices) { prices?.deals?.minByOrNull { it.price?.amount ?: Double.MAX_VALUE } }
    val topBarHeightPx = heroTopBarHeightPx()
    val isPastHero by remember(topBarHeightPx) { derivedStateOf { listState.isPastHero(topBarHeightPx) } }
    HeroStatusBarIcons(isPastHero)

    // Keep this order in step with StoresItemIndex.
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeroButton(
                            icon = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            onClick = {
                                navController.navigateUp()
                            }
                        )
                        Box(Modifier.weight(1f))
                        HeroButton(
                            icon = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(
                                        Intent.EXTRA_TEXT,
                                        listOfNotNull(
                                            game.title ?: viewModel.title,
                                            game.urls?.game
                                        ).joinToString("\n")
                                    )
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                        )
                        FavoriteButton(isFav = isFav, onClick = {
                            viewModel.toggleFavDeal(
                                game,
                                prices = prices?.deals ?: emptyList()
                            )
                        })
                    }
                },
                colors = heroTopBarColors(isPastHero),
            )
        }
    ) {_->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "hero") {
                Hero(
                    imageUrl = game.assets?.banner600 ?: game.assets?.banner400
                    ?: game.assets?.boxart,
                )
            }
            item(key = "title") { TitleBlock(game = game, fallbackTitle = viewModel.title) }
            item(key = "quick_facts") { QuickFacts(game) }
            item(key = "best_price") {
                BestPriceSection(
                    isLoading = gamePriceInfo.isLoading,
                    bestDeal = bestDeal,
                    onOpen = { openUrl(bestDeal?.url) },
                    onChooseStore = { scope.launch { listState.animateScrollToItem(StoresItemIndex, -topBarHeightPx) } },
                )
            }
            item(key = "price_history") {
                if (prices != null) {
                    PriceHistorySection(
                        prices = prices,
                        bestDeal = bestDeal,
                        history = priceHistory.data
                    )
                }
            }
            // Stays in the list even when empty so StoresItemIndex always holds.
            item(key = "stores") {
                StorePrices(deals = prices?.deals.orEmpty(), onOpen = { openUrl(it.url) })
            }
        }
    }
}

@Composable
private fun TitleBlock(game: GameInfo, fallbackTitle: String?) {
    val colors = MaterialTheme.colorScheme
    val developer = game.developers.firstOrNull()?.name
    val publisher = game.publishers.firstOrNull()?.name
    val released = game.releaseDate?.takeIf { it.isNotBlank() }
        ?.let { stringResource(R.string.released_on, formatReleaseDate(it)) }
    val earlyAccess = stringResource(R.string.early_access)
    val tags = remember(game, earlyAccess) {
        (if (game.earlyAccess == true) listOf(earlyAccess) else emptyList()) + game.tags
    }

    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = game.title ?: fallbackTitle.orEmpty(),
            style = MaterialTheme.typography.displayMediumEmphasized.copy(fontSize = 40.sp, lineHeight = 44.sp),
        )
        val meta = listOfNotNull(developer, released).joinToString(" · ")
        if (meta.isNotEmpty()) {
            Text(text = meta, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
        }
        if (publisher != null && publisher != developer) {
            Text(
                text = "${stringResource(R.string.publisher)} · $publisher",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        if (tags.isNotEmpty()) {
            ExpandableTags(tags = tags, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun QuickFacts(game: GameInfo) {
    val review = game.reviews.firstOrNull { it.source == "Steam" && it.score != null }
        ?: game.reviews.firstOrNull { it.score != null }
    val rank = game.stats?.rank?.takeIf { it > 0 }
    val factStyle = PriceTextStyles.medium.copy(fontSize = 22.sp)

    QuickFactsRow(
        buildList {
            if (review != null) {
                add(
                    QuickFact(
                        value = {
                            Text(stringResource(R.string.score_percent_format, review.score ?: 0), style = factStyle)
                        },
                        label = listOfNotNull(review.source, review.count?.let(::getTotalReviews)).joinToString(" · "),
                    )
                )
            }
            if (rank != null) {
                add(
                    QuickFact(
                        value = { Text("#$rank", style = factStyle) },
                        label = stringResource(R.string.rank),
                    )
                )
            }
            // Only "mature" is flagged; unflagged games aren't necessarily suitable for all ages.
            if (game.mature == true) {
                add(
                    QuickFact(
                        value = { Text(stringResource(R.string.mature_18_plus), style = factStyle) },
                        label = stringResource(R.string.age_rating),
                    )
                )
            }
        }
    )
}
