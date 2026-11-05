package com.rkbapps.gdealz.ui.tab.free

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.FreeDealsFilter
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealPoster
import com.rkbapps.gdealz.ui.tab.deals.composables.DiscountBadge
import com.rkbapps.gdealz.ui.tab.free.composables.FilterBottomSheetExpressive
import com.rkbapps.gdealz.ui.theme.GroupedListGap
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.toPlatformList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import androidx.compose.ui.platform.LocalLocale

private const val TabUnclaimed = 0
private const val TabClaimed = 1

/** The featured card's corners: hero on top, then it joins the grouped list below. */
private val FeaturedShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 8.dp, bottomEnd = 8.dp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FreeDealsTabExpressive(
    navController: NavHostController,
    viewModel: FreeDealsViewModel = hiltViewModel()
) {
    val stores by viewModel.stores.collectAsStateWithLifecycle()
    val types by viewModel.types.collectAsStateWithLifecycle()
    val unclaimed by viewModel.unClaimedGiveaway.collectAsStateWithLifecycle()
    val claimed by viewModel.claimedGiveaway.collectAsStateWithLifecycle()
    val giveawayState by viewModel.giveawayState.collectAsStateWithLifecycle()
    val appliedFilter by viewModel.filter.collectAsStateWithLifecycle()

    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    var showFilterSheet by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val now = rememberNow()

    val openDetails: (Giveaway) -> Unit = { navController.navigate(Routes.FreeGameDetails(it.id)) }
    val closeSheet = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) showFilterSheet = false
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
        ) {
            FilterBottomSheetExpressive(
                stores = stores.toList(),
                types = types,
                appliedFilters = appliedFilter,
                resultCount = viewModel::countUnclaimed,
                onCancel = { closeSheet() },
            ) {
                viewModel.updateFilter(it)
                closeSheet()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    FreeHeader(
                        liveCount = unclaimed.count { isActive(it, now) },
                        isFiltered = appliedFilter != FreeDealsFilter(),
                        onFilter = { showFilterSheet = true }
                    )
                }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = innerPadding.calculateTopPadding())
        ) {


            ClaimStatusGroup(
                selected = pagerState.currentPage,
                unclaimedCount = unclaimed.size,
                claimedCount = claimed.size,
                onSelect = { scope.launch { pagerState.animateScrollToPage(it) } }
            )

            if (appliedFilter != FreeDealsFilter() && pagerState.currentPage == TabUnclaimed) {
                AssistChip(
                    onClick = { viewModel.clearFilter() },
                    label = { Text(stringResource(R.string.clear_filter)) },
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            when {
                giveawayState.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
                }

                giveawayState.error != null -> ErrorScreen(
                    giveawayState.error ?: stringResource(R.string.unknown)
                )

                else -> HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(GroupedListGap * 2),
                    ) {
                        if (page == TabUnclaimed) {
                            unclaimedItems(
                                giveaways = unclaimed,
                                now = now,
                                onOpen = openDetails,
                                onMarkClaimed = { viewModel.setClaimed(it, true) }
                            )
                        } else {
                            claimedItems(
                                giveaways = claimed,
                                onOpen = openDetails,
                                onUndo = { viewModel.setClaimed(it, false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Current time that ticks every minute, so countdowns stay honest while the tab is open. */
@Composable
internal fun rememberNow(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.minutes)
            now = System.currentTimeMillis()
        }
    }
    return now
}

private val apiDateFormat get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

internal fun parseApiDate(value: String): Long? =
    runCatching { apiDateFormat.parse(value)?.time }.getOrNull()

private fun isActive(giveaway: Giveaway, now: Long): Boolean =
    parseApiDate(giveaway.endDate)?.let { it > now } ?: true

/** GamerPower lists "PC" alongside the actual storefront; the storefront is what users care about. */
internal fun Giveaway.storeName(): String {
    val platforms = platforms.toPlatformList()
    return platforms.firstOrNull { it != "PC" && it != "DRM-Free" } ?: platforms.firstOrNull() ?: ""
}

@Composable
private fun remainingText(endMillis: Long, now: Long): String {
    val minutes = ((endMillis - now) / 60_000).coerceAtLeast(0)
    val days = minutes / (60 * 24)
    val hours = (minutes / 60) % 24
    return when {
        minutes <= 0 -> stringResource(R.string.giveaway_ended)
        days > 0 -> stringResource(R.string.duration_days_hours, days, hours)
        hours > 0 -> stringResource(R.string.duration_hours_minutes, hours, minutes % 60)
        else -> stringResource(R.string.duration_minutes, minutes)
    }
}

@Composable
private fun FreeHeader(liveCount: Int, isFiltered: Boolean, onFilter: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text = pluralStringResource(R.plurals.giveaways_live, liveCount, liveCount),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = MaterialTheme.extendedColors.giftContent,
            )
            Text(
                text = stringResource(R.string.free_to_keep),
                style = MaterialTheme.typography.displayMediumEmphasized,
            )
        }
        FilledIconButton(
            onClick = onFilter,
            shapes = IconButtonShapes(
                shape = IconButtonDefaults.mediumSquareShape,
                pressedShape = IconButtonDefaults.mediumPressedShape
            ),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isFiltered) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = if (isFiltered) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(bottom = 4.dp)
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.filter),
                contentDescription = stringResource(R.string.filter_giveaways),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ClaimStatusGroup(
    selected: Int,
    unclaimedCount: Int,
    claimedCount: Int,
    onSelect: (Int) -> Unit,
) {
    val gift = MaterialTheme.extendedColors
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        listOf(
            Triple(TabUnclaimed, R.string.un_claimed, unclaimedCount),
            Triple(TabClaimed, R.string.claimed, claimedCount),
        ).forEach { (tab, label, count) ->
            val checked = selected == tab
            ToggleButton(
                checked = checked,
                onCheckedChange = { onSelect(tab) },
                shapes = if (tab == TabUnclaimed) ButtonGroupDefaults.connectedLeadingButtonShapes()
                else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = ToggleButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = gift.giftFill,
                    checkedContentColor = gift.onGiftFill,
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .semantics { role = Role.Tab }
            ) {
                Text(stringResource(label), style = MaterialTheme.typography.titleSmallEmphasized, maxLines = 1)
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .sizeIn(minWidth = 22.dp, minHeight = 22.dp)
                        .background(
                            color = if (checked) gift.onGiftFill else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = if (checked) gift.giftFill else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun LazyListScope.unclaimedItems(
    giveaways: List<Giveaway>,
    now: Long,
    onOpen: (Giveaway) -> Unit,
    onMarkClaimed: (Giveaway) -> Unit,
) {
    if (giveaways.isEmpty()) {
        item(key = "empty") { ErrorScreen(stringResource(R.string.no_active_giveaways)) }
        return
    }
    val featured = giveaways.first()
    item(key = featured.id) {
        FeaturedGiveaway(
            giveaway = featured,
            now = now,
            shape = if (giveaways.size == 1) MaterialTheme.shapes.extraLarge else FeaturedShape,
            onOpen = { onOpen(featured) },
            onMarkClaimed = { onMarkClaimed(featured) },
            modifier = Modifier.animateItem()
        )
    }
    val rest = giveaways.drop(1)
    itemsIndexed(rest, key = { _, it -> it.id }) { index, giveaway ->
        // Index + 1 because the featured card already occupies the top of the group.
        GiveawayRow(
            giveaway = giveaway,
            shape = GroupedListShapes.forIndex(index + 1, rest.size + 1),
            onClick = { onOpen(giveaway) },
            trailing = {
                parseApiDate(giveaway.endDate)?.let { end ->
                    Text(
                        text = stringResource(R.string.time_left, remainingText(end, now)),
                        style = MaterialTheme.typography.labelLargeEmphasized,
                        color = MaterialTheme.extendedColors.giftContent,
                    )
                }
            },
            modifier = Modifier.animateItem()
        )
    }
}

private fun LazyListScope.claimedItems(
    giveaways: List<Giveaway>,
    onOpen: (Giveaway) -> Unit,
    onUndo: (Giveaway) -> Unit,
) {
    if (giveaways.isEmpty()) {
        item(key = "empty") { ErrorScreen(stringResource(R.string.nothing_claimed_yet)) }
        return
    }
    itemsIndexed(giveaways, key = { _, it -> it.id }) { index, giveaway ->
        val haptics = LocalHapticFeedback.current
        GiveawayRow(
            giveaway = giveaway,
            shape = GroupedListShapes.forIndex(index, giveaways.size),
            onClick = { onOpen(giveaway) },
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onUndo(giveaway)
            },
            trailing = { InLibraryTag() },
            modifier = Modifier.animateItem()
        )
    }
    item(key = "claimed_hint") {
        Text(
            text = stringResource(R.string.claimed_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun FeaturedGiveaway(
    giveaway: Giveaway,
    now: Long,
    shape: Shape,
    onOpen: () -> Unit,
    onMarkClaimed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val gift = MaterialTheme.extendedColors
    val end = parseApiDate(giveaway.endDate)
    val published = parseApiDate(giveaway.publishedDate)
    val store = giveaway.storeName()

    Surface(
        onClick = onOpen,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box {
                DealPoster(
                    url = giveaway.image,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                DiscountBadge(
                    cut = 100,
                    size = 72.dp,
                    textStyle = PriceTextStyles.small.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(14.dp)
                )
            }
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = giveaway.title,
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        MetaLine(giveaway, showWorth = true)
                    }
                    TypeTag(giveaway.type)
                }

                if (end != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.ends_in, remainingText(end, now)),
                                style = MaterialTheme.typography.labelLargeEmphasized
                            )
                            Text(
                                text = SimpleDateFormat("EEE, d MMM · h:mm a", LocalLocale.current.platformLocale).format(end),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Time left in the offer, draining as the deadline approaches.
                        val remaining = if (published != null && end > published) {
                            ((end - now).toFloat() / (end - published)).coerceIn(0f, 1f)
                        } else 1f
                        LinearWavyProgressIndicator(
                            progress = { remaining },
                            color = gift.giftContent,
                            trackColor = gift.giftContainer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { uriHandler.openUri(giveaway.openGiveawayUrl) },
                        shapes = ButtonDefaults.shapes(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = gift.giftFill,
                            contentColor = gift.onGiftFill
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (store.isNotEmpty()) stringResource(R.string.claim_on, store)
                            else stringResource(R.string.claim),
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Icon(
                            Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(18.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onMarkClaimed,
                        shapes = IconButtonShapes(
                            shape = IconButtonDefaults.mediumSquareShape,
                            pressedShape = IconButtonDefaults.mediumPressedShape
                        ),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.size(width = 64.dp, height = 48.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = stringResource(R.string.mark_as_claimed, giveaway.title)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GiveawayRow(
    giveaway: Giveaway,
    shape: Shape,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DealPoster(
                url = giveaway.thumbnail,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.large)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = giveaway.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                MetaLine(giveaway, showWorth = false)
            }
            trailing()
        }
    }
}

@Composable
private fun MetaLine(giveaway: Giveaway, showWorth: Boolean) {
    Row {
        Text(
            text = listOf(giveaway.storeName(), giveaway.type).filter { it.isNotBlank() }.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (showWorth && giveaway.worth.isNotBlank() && giveaway.worth != "N/A") {
            Text(
                text = " · ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = giveaway.worth,
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TypeTag(type: String) {
    Text(
        text = type.uppercase(),
        style = MaterialTheme.typography.labelMediumEmphasized,
        maxLines = 1,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun InLibraryTag() {
    val deal = MaterialTheme.extendedColors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(deal.dealContainer, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Icon(
            Icons.Rounded.Check,
            contentDescription = null,
            tint = deal.onDealContainer,
            modifier = Modifier.size(14.dp)
        )
        Box(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.in_library),
            style = MaterialTheme.typography.labelMediumEmphasized,
            color = deal.onDealContainer
        )
    }
}
