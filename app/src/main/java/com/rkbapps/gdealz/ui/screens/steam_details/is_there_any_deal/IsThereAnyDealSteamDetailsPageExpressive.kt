package com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Data
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.models.price.PriceHistoryEntry
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.tab.deals.composables.DealPoster
import com.rkbapps.gdealz.ui.tab.deals.composables.DiscountBadge
import com.rkbapps.gdealz.ui.theme.BrandShapes
import com.rkbapps.gdealz.ui.theme.GroupedListGap
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.StoreUtil
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

internal val PageSidePadding = 16.dp

/** Tags shown before the rest fold behind a "+N" chip. */
private const val CollapsedTagCount = 6

// Position of "Prices across stores" in the list; the split button's arrow scrolls to it.
internal const val StoresItemIndex = 5

/** Sits on artwork, so it is the same in both themes. */
private val HeroButtonScrim = Color(0xA8131318)

private val Deals.cutPercent get() = cut ?: 0
private val Deals.isFree get() = cut == 100 || price?.amount == 0.0

@Composable
private fun Deals.currentPriceText(): String =
    if (isFree) stringResource(R.string.free) else CurrencyAndCountryUtil.getCurrencyAndAmount(price)

@Composable
private fun Deals.saleEndsText(): String? = parseIsoDate(expiry)?.let {
    stringResource(R.string.sale_ends_on, SimpleDateFormat("EEE, d MMM", LocalLocale.current.platformLocale).format(it))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IsThereAnyDealSteamDetailsPageExpressive(
    navController: NavHostController,
    viewModel: IsThereAnyDealSteamViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val gameData by viewModel.gameInfo.collectAsStateWithLifecycle()
    val gamePriceInfo by viewModel.gamePriceInfo.collectAsStateWithLifecycle()
    val priceHistory by viewModel.priceHistory.collectAsStateWithLifecycle()
    val steamGameData by viewModel.steamGameData.collectAsStateWithLifecycle()
    val isFav by viewModel.isFavDeal

    val openUrl: (String?) -> Unit = { url ->
        try {
            url?.let { uriHandler.openUri(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, R.string.unable_to_open_website, Toast.LENGTH_SHORT).show()
        }
    }

    // Games that aren't on Steam have no details here; the plain info screen covers them.
    LaunchedEffect(steamGameData.error) {
        if (steamGameData.error != null) {
            navController.navigate(Routes.GameInfo(gameId = viewModel.gameId, title = viewModel.title)) {
                popUpTo<Routes.IsThereAnyDealSteamGameDetails> { inclusive = true }
            }
        }
    }

    val steam = steamGameData.data?.data
    if (steam == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
        }
        return
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val prices = gamePriceInfo.data
    val bestDeal = remember(prices) { prices?.deals?.minByOrNull { it.price?.amount ?: Double.MAX_VALUE } }
    val topBarHeightPx = heroTopBarHeightPx()
    val isPastHero by remember(topBarHeightPx) { derivedStateOf { listState.isPastHero(topBarHeightPx) } }
    HeroStatusBarIcons(isPastHero)

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
                            onClick = {  val link = gameData.data?.urls?.game ?: steam.website
                                val intent = Intent(Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(
                                        Intent.EXTRA_TEXT,
                                        listOfNotNull(
                                            steam.name ?: viewModel.title,
                                            link
                                        ).joinToString("\n")
                                    )
                                context.startActivity(Intent.createChooser(intent, null)) }
                        )
                        FavoriteButton(isFav = isFav, onClick = {
                            gameData.data?.let {
                                viewModel.toggleFavDeal(
                                    it,
                                    prices = prices?.deals ?: emptyList()
                                )
                            }
                        })
                    }
                },
                colors = heroTopBarColors(isPastHero),
            )
        }
    ) { _ ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "hero") {
                Hero(
                    imageUrl = steam.screenshots.firstOrNull()?.pathFull ?: steam.headerImage,
                )
            }
            item(key = "title") { TitleBlock(steam = steam, fallbackTitle = viewModel.title) }
            item(key = "quick_facts") {
                QuickFacts(
                    steam = steam,
                    game = gameData.data,
                    onOpenMetacritic = { openUrl(steam.metacritic?.url) })
            }
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
            if (steam.screenshots.isNotEmpty()) {
                item(key = "screenshots") {
                    with(sharedTransitionScope) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SectionTitle(
                                title = stringResource(R.string.screenshots),
                                trailing = steam.screenshots.size.toString(),
                                modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp)
                            )
                            HorizontalMultiBrowseCarousel(
                                state = rememberCarouselState { steam.screenshots.size },
                                preferredItemWidth = 232.dp,
                                itemSpacing = 8.dp,
                                contentPadding = PaddingValues(horizontal = PageSidePadding),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                            ) { index ->
                                val url = steam.screenshots[index].pathFull
                                AsyncImage(
                                    model = url,
                                    contentDescription = stringResource(
                                        R.string.screenshot_position,
                                        index + 1,
                                        steam.screenshots.size
                                    ),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .maskClip(MaterialTheme.shapes.extraLarge)
                                        .sharedElement(
                                            rememberSharedContentState(key = url ?: ""),
                                            animatedVisibilityScope = animatedContentScope
                                        )
                                        .clickable {
                                            navController.navigate(
                                                Routes.ScreenshotViewer(
                                                    imageUrls = steam.screenshots.mapNotNull { it.pathFull },
                                                    startIndex = index
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }
            item(key = "about") { About(steam = steam, onOpenWebsite = { openUrl(steam.website) }) }
            val minimum = steam.pcRequirements?.minimum?.let(::parseRequirements).orEmpty()
            val recommended = steam.pcRequirements?.recommended?.let(::parseRequirements).orEmpty()
            if (minimum.isNotEmpty() || recommended.isNotEmpty()) {
                item(key = "requirements") {
                    SystemRequirements(
                        minimum = minimum,
                        recommended = recommended
                    )
                }
            }
        }
    }
}

@Composable
internal fun Hero(
    imageUrl: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
    ) {
        DealPoster(url = imageUrl, modifier = Modifier.fillMaxSize())
        HeroStatusBarScrim()
    }
}

/** Keeps the light status bar icons readable on bright art. */
@Composable
internal fun HeroStatusBarScrim() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
    )
}

/** Light status bar icons over the hero art; the theme's own icons once the bar turns solid. */
@Composable
internal fun HeroStatusBarIcons(isPastHero: Boolean) {
    val window = LocalActivity.current?.window
    val isLightSurface = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    DisposableEffect(window, isPastHero, isLightSurface) {
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        if (!isPastHero) controller?.isAppearanceLightStatusBars = false
        onDispose {
            // Past the hero nothing was changed, and the screenshot viewer may own the icons by now.
            if (!isPastHero) controller?.isAppearanceLightStatusBars = isLightSurface
        }
    }
}

/** Full height of a top bar drawn over the hero, status bar included. */
@Composable
internal fun heroTopBarHeightPx(): Int {
    val density = LocalDensity.current
    return WindowInsets.statusBars.getTop(density) +
        with(density) { TopAppBarDefaults.TopAppBarExpandedHeight.roundToPx() }
}

/** True once the first item (the hero) no longer fills the area behind the top bar. */
internal fun LazyListState.isPastHero(topBarHeightPx: Int): Boolean {
    val hero = layoutInfo.visibleItemsInfo.firstOrNull() ?: return false
    return hero.index > 0 || hero.size + hero.offset < topBarHeightPx
}

/** Clear over the hero art, solid once page content would otherwise run under the clock and buttons. */
@Composable
internal fun heroTopBarColors(isPastHero: Boolean): TopAppBarColors {
    val surface = MaterialTheme.colorScheme.surface
    val container by animateColorAsState(
        targetValue = if (isPastHero) surface else surface.copy(alpha = 0f),
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    )
    return TopAppBarDefaults.topAppBarColors(
        containerColor = container,
        scrolledContainerColor = container,
    )
}

@Composable
internal fun HeroButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = HeroButtonScrim,
        contentColor = Color.White,
        modifier = modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(22.dp))
        }
    }
}

/** Grows into a "Saved" pill so saved state doesn't rely on the heart's fill alone. */
@Composable
fun FavoriteButton(isFav: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val corner by animateDpAsState(
        targetValue = if (isFav) 16.dp else 24.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(corner),
        color = if (isFav) colors.errorContainer else HeroButtonScrim,
        contentColor = if (isFav) colors.onErrorContainer else Color.White,
        modifier = Modifier
            .height(48.dp)
            .animateContentSize(MaterialTheme.motionScheme.fastSpatialSpec())
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = stringResource(R.string.favorite_icon),
                modifier = Modifier.size(22.dp)
            )
            if (isFav) {
                Text(stringResource(R.string.saved), style = MaterialTheme.typography.labelLargeEmphasized)
            }
        }
    }
}

@Composable
private fun TitleBlock(steam: Data, fallbackTitle: String?) {
    val released = if (steam.releaseDate?.comingSoon == true) {
        "${stringResource(R.string.coming)} ${stringResource(R.string.soon)}"
    } else {
        steam.releaseDate?.date?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.released_on, it) }
    }
    val tags = remember(steam) {
        (steam.genres.mapNotNull { it.description } + steam.categories.mapNotNull { it.description }).distinct()
    }

    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = steam.name ?: fallbackTitle.orEmpty(),
            style = MaterialTheme.typography.displayMediumEmphasized.copy(fontSize = 40.sp, lineHeight = 44.sp),
        )
        Text(
            text = listOfNotNull(steam.developers.firstOrNull(), released).joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ExpandableTags(tags = tags, modifier = Modifier.padding(top = 4.dp))
    }
}

/** Shows the first few tags; the rest fold behind a "+N" chip. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExpandableTags(tags: List<String>, modifier: Modifier = Modifier) {
    var showAllTags by rememberSaveable { mutableStateOf(false) }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.animateContentSize()
    ) {
        val visible = if (showAllTags) tags else tags.take(CollapsedTagCount)
        visible.forEach { TagChip(it) }
        if (visible.size < tags.size) {
            TagChip(text = "+${tags.size - visible.size}", onClick = { showAllTags = true })
        }
    }
}

@Composable
private fun TagChip(text: String, onClick: (() -> Unit)? = null) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

internal class QuickFact(val value: @Composable () -> Unit, val label: String, val onClick: (() -> Unit)? = null)

@Composable
private fun QuickFacts(steam: Data, game: GameInfo?, onOpenMetacritic: () -> Unit) {
    val metacritic = steam.metacritic?.score
    // required_age arrives as "0", "18" or "18.0" depending on the game.
    val age = steam.requiredAge?.toDoubleOrNull()?.toInt()?.takeIf { it > 0 }
    val review = game?.reviews?.let { reviews ->
        reviews.firstOrNull { it.source == "Steam" && it.score != null } ?: reviews.firstOrNull { it.score != null }
    }
    val factStyle = PriceTextStyles.medium.copy(fontSize = 22.sp)

    val facts = buildList {
        if (metacritic != null) {
            add(
                QuickFact(
                    value = {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    BrandShapes.score
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = metacritic.toString(),
                                style = PriceTextStyles.medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    label = stringResource(R.string.meta_critic),
                    onClick = onOpenMetacritic.takeIf { steam.metacritic?.url != null }
                )
            )
        }
        if (age != null) {
            add(
                QuickFact(
                    value = { Text(stringResource(R.string.age_limit_format, age.toString()), style = factStyle) },
                    label = stringResource(R.string.age_rating)
                )
            )
        }
        if (review != null) {
            add(
                QuickFact(
                    value = {
                        Text(stringResource(R.string.score_percent_format, review.score ?: 0), style = factStyle)
                    },
                    label = listOfNotNull(review.source, review.count?.let(::getTotalReviews)).joinToString(" · ")
                )
            )
        }
    }
    QuickFactsRow(facts)
}

@Composable
internal fun QuickFactsRow(facts: List<QuickFact>) {
    if (facts.isEmpty()) return

    Row(
        modifier = Modifier
            .padding(horizontal = PageSidePadding)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        facts.forEachIndexed { index, fact ->
            val shape = RoundedCornerShape(
                topStart = if (index == 0) 24.dp else 8.dp,
                bottomStart = if (index == 0) 24.dp else 8.dp,
                topEnd = if (index == facts.lastIndex) 24.dp else 8.dp,
                bottomEnd = if (index == facts.lastIndex) 24.dp else 8.dp,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .then(if (fact.onClick != null) Modifier.clickable(onClick = fact.onClick) else Modifier)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.height(52.dp), contentAlignment = Alignment.Center) { fact.value() }
                Text(
                    text = fact.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
internal fun BestPriceSection(
    isLoading: Boolean,
    bestDeal: Deals?,
    onOpen: () -> Unit,
    onChooseStore: () -> Unit,
) {
    when {
        isLoading -> LinearWavyProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PageSidePadding)
        )

        bestDeal != null -> BestPriceCard(deal = bestDeal, onOpen = onOpen, onChooseStore = onChooseStore)

        else -> Text(
            text = stringResource(R.string.no_prices_available),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PageSidePadding)
        )
    }
}

@Composable
private fun BestPriceCard(deal: Deals, onOpen: () -> Unit, onChooseStore: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val store = deal.shop?.name.orEmpty()
    val buttonColors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary)
    val buttonHeight = SplitButtonDefaults.MediumContainerHeight

    Surface(
        shape = MaterialTheme.shapes.extraLargeIncreased,
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PageSidePadding)
    ) {
        Box {
            if (deal.cutPercent > 0) {
                DiscountBadge(
                    cut = deal.cutPercent,
                    size = 76.dp,
                    textStyle = PriceTextStyles.medium,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(18.dp)
                )
            }
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = stringResource(R.string.best_price_right_now).uppercase(),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = deal.currentPriceText(),
                        style = PriceTextStyles.large.copy(fontSize = 40.sp, lineHeight = 44.sp),
                        maxLines = 1,
                    )
                    if (deal.cutPercent > 0) {
                        Text(
                            text = CurrencyAndCountryUtil.getCurrencyAndAmount(deal.regular),
                            style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                            maxLines = 1,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                Text(
                    text = listOfNotNull(stringResource(R.string.at_store, store), deal.saleEndsText()).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                )
                SplitButtonLayout(
                    modifier = Modifier.fillMaxWidth(),
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            onClick = onOpen,
                            shapes = SplitButtonDefaults.leadingButtonShapesFor(buttonHeight),
                            colors = buttonColors,
                            contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(buttonHeight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(buttonHeight),
                        ) {
                            Text(
                                text = stringResource(R.string.get_it_on, store),
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(18.dp)
                            )
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            onClick = onChooseStore,
                            shapes = SplitButtonDefaults.trailingButtonShapesFor(buttonHeight),
                            colors = buttonColors,
                            contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(buttonHeight),
                            modifier = Modifier
                                .width(56.dp)
                                .height(buttonHeight),
                        ) {
                            Icon(
                                Icons.Rounded.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.choose_another_store),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun SectionTitle(title: String, trailing: String?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmallEmphasized)
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
    }
}

@Composable
internal fun PriceHistorySection(prices: PriceDetail, bestDeal: Deals?, history: List<PriceHistoryEntry>?) {
    val now = remember { System.currentTimeMillis() }
    val points = remember(history, prices) { lowestPriceSeries(history.orEmpty(), prices.deals, now) }
    val current = bestDeal?.price?.amount
    val lows = prices.historyLow

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PageSidePadding)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle(
                title = stringResource(R.string.price_history),
                trailing = stringResource(R.string.last_12_months_all_stores)
            )
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LowTile(
                    label = stringResource(R.string.lowest_ever),
                    price = lows?.all,
                    isToday = lows?.all?.amount == current,
                    highlighted = true,
                    startCorner = 18.dp,
                    endCorner = 6.dp,
                    modifier = Modifier.weight(1f)
                )
                LowTile(
                    label = stringResource(R.string.three_months),
                    price = lows?.m3,
                    isToday = lows?.m3?.amount == current,
                    highlighted = false,
                    startCorner = 6.dp,
                    endCorner = 6.dp,
                    modifier = Modifier.weight(1f)
                )
                LowTile(
                    label = stringResource(R.string.one_year),
                    price = lows?.y1,
                    isToday = lows?.y1?.amount == current,
                    highlighted = false,
                    startCorner = 6.dp,
                    endCorner = 18.dp,
                    modifier = Modifier.weight(1f)
                )
            }
            // Two points or fewer would be a flat line that says nothing.
            AnimatedVisibility(visible = history != null && points.size > 2) {
                PriceHistoryChart(points = points, lowestEver = lows?.all?.amount, end = now)
            }
        }
    }
}

@Composable
private fun LowTile(
    label: String,
    price: com.rkbapps.gdealz.models.deal.Price?,
    isToday: Boolean,
    highlighted: Boolean,
    startCorner: Dp,
    endCorner: Dp,
    modifier: Modifier = Modifier,
) {
    val extended = MaterialTheme.extendedColors
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (highlighted) extended.dealContainer else colors.surfaceContainerHigh,
                shape = RoundedCornerShape(
                    topStart = startCorner,
                    bottomStart = startCorner,
                    topEnd = endCorner,
                    bottomEnd = endCorner
                )
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmallEmphasized,
            color = if (highlighted) extended.onDealContainer else colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = price?.amount?.let { CurrencyAndCountryUtil.getCurrencyAndAmount(price) }
                ?: stringResource(R.string.not_available),
            style = PriceTextStyles.small,
            color = if (highlighted) extended.onDealContainer else colors.onSurface,
            maxLines = 1,
        )
        if (isToday) {
            Text(
                text = stringResource(R.string.thats_today),
                style = MaterialTheme.typography.labelSmall,
                color = if (highlighted) extended.onDealContainer else colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun StorePrices(deals: List<Deals>, onOpen: (Deals) -> Unit) {
    if (deals.isEmpty()) return
    val sorted = remember(deals) { deals.sortedBy { it.price?.amount ?: Double.MAX_VALUE } }
    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding),
        verticalArrangement = Arrangement.spacedBy(GroupedListGap)
    ) {
        Text(
            text = stringResource(R.string.prices_across_stores),
            style = MaterialTheme.typography.headlineSmallEmphasized,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
        )
        sorted.forEachIndexed { index, deal ->
            StorePriceRow(
                deal = deal,
                shape = GroupedListShapes.forIndex(index, sorted.size),
                onClick = { onOpen(deal) }
            )
        }
    }
}

@Composable
private fun StorePriceRow(deal: Deals, shape: Shape, onClick: () -> Unit) {
    val extended = MaterialTheme.extendedColors
    val colors = MaterialTheme.colorScheme
    val storeImage = remember(deal.shop?.id) { StoreUtil.getStoreImage(deal.shop?.id ?: 0) }
    val note = listOfNotNull(
        deal.drm.mapNotNull { it.name }.joinToString(", ").takeIf { it.isNotEmpty() },
        deal.voucher?.takeIf { it.isNotBlank() },
        deal.saleEndsText() ?: stringResource(R.string.full_price).takeIf { deal.cutPercent == 0 },
    ).joinToString(" · ")

    Surface(
        onClick = onClick,
        shape = shape,
        color = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 64.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    // Most store logos are white, so this stays dark in both themes.
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                if (storeImage != null) {
                    AsyncImage(
                        model = storeImage,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.console),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                } else {
                    Text(text = deal.shop?.name?.take(1).orEmpty(), style = PriceTextStyles.small, color = Color.White)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = deal.shop?.name.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (note.isNotEmpty()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = deal.currentPriceText(), style = PriceTextStyles.small, maxLines = 1)
                Text(
                    text = if (deal.cutPercent > 0) "-${deal.cutPercent}%" else "—",
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    color = if (deal.cutPercent > 0) extended.dealContent else colors.onSurfaceVariant,
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = stringResource(R.string.open_deal),
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun About(steam: Data, onOpenWebsite: () -> Unit) {
    val description = remember(steam.shortDescription) {
        steam.shortDescription?.let { AnnotatedString.fromHtml(it) }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var overflows by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.about), style = MaterialTheme.typography.headlineSmallEmphasized)
        Text(
            text = description ?: AnnotatedString(stringResource(R.string.nothing_here)),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 23.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) overflows = it.hasVisualOverflow },
            modifier = Modifier.animateContentSize()
        )
        steam.publishers.firstOrNull()?.let {
            Text(
                text = "${stringResource(R.string.publisher)} · $it",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (overflows) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = stringResource(if (expanded) R.string.show_less else R.string.read_more),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                    )
                }
            }
            if (!steam.website.isNullOrBlank()) {
                TextButton(onClick = onOpenWebsite) {
                    Text(
                        text = stringResource(R.string.go_official_website),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                    )
                }
            }
        }
    }
}

private data class Requirement(val name: String, val value: String)

private val RequirementRow = Regex("<li>\\s*<strong>(.*?)</strong>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)

/** Steam sends requirements as an HTML list of `<li><strong>OS:</strong> Windows 10</li>`. */
private fun parseRequirements(html: String): List<Requirement> {
    val rows = RequirementRow.findAll(html).map { match ->
        Requirement(
            name = AnnotatedString.fromHtml(match.groupValues[1]).text.trim().trimEnd(':', '*', ' '),
            value = AnnotatedString.fromHtml(match.groupValues[2]).text.trim()
        )
    }.filter { it.name.isNotEmpty() && it.value.isNotEmpty() }.toList()
    if (rows.isNotEmpty()) return rows
    // Older listings use free text; show it whole rather than dropping it.
    val text = AnnotatedString.fromHtml(html).text.trim()
    return if (text.isEmpty()) emptyList() else listOf(Requirement("", text))
}

@Composable
private fun SystemRequirements(minimum: List<Requirement>, recommended: List<Requirement>) {
    val levels = listOfNotNull(
        (R.string.minimum to minimum).takeIf { minimum.isNotEmpty() },
        (R.string.recommended to recommended).takeIf { recommended.isNotEmpty() },
    )
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PageSidePadding)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Memory, contentDescription = null, tint = colors.primary)
                Text(
                    text = stringResource(R.string.system_requirements),
                    style = MaterialTheme.typography.headlineSmallEmphasized
                )
            }
            if (levels.size > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                    levels.forEachIndexed { index, (label, _) ->
                        ToggleButton(
                            checked = selected == index,
                            onCheckedChange = { selected = index },
                            shapes = if (index == 0) {
                                ButtonGroupDefaults.connectedLeadingButtonShapes()
                            } else {
                                ButtonGroupDefaults.connectedTrailingButtonShapes()
                            },
                            colors = ToggleButtonDefaults.colors(
                                containerColor = colors.surfaceContainerHigh,
                                contentColor = colors.onSurface,
                                checkedContainerColor = colors.primary,
                                checkedContentColor = colors.onPrimary,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                        ) {
                            Text(stringResource(label), style = MaterialTheme.typography.labelLargeEmphasized)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                levels.getOrNull(selected)?.second?.forEach { requirement ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (requirement.name.isNotEmpty()) {
                            Text(
                                text = requirement.name,
                                style = MaterialTheme.typography.bodySmallEmphasized,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.width(88.dp)
                            )
                        }
                        Text(text = requirement.value, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
