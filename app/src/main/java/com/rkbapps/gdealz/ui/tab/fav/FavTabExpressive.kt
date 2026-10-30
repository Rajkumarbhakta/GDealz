package com.rkbapps.gdealz.ui.tab.fav

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.tab.deals.composables.DealPoster
import com.rkbapps.gdealz.ui.theme.BrandShapes
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.Store
import com.rkbapps.gdealz.util.StoreUtil
import kotlinx.coroutines.launch

@Composable
fun FavTabExpressive(
    navController: NavHostController,
    viewModel: FavViewModel = hiltViewModel()
) {
    val favList by viewModel.favList.collectAsStateWithLifecycle()
    val favStoreIds by viewModel.favStoreIds.collectAsStateWithLifecycle()
    val favoriteStoreIds = favStoreIds?.ids.orEmpty()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Biggest drop first; games without price data sink to the bottom.
    val watchlist = remember(favList) { favList.sortedByDescending { it.discountPercentage ?: -1.0 } }

    val removedMessage = stringResource(R.string.removed_from_watchlist)
    val clearedMessage = stringResource(R.string.watchlist_cleared)
    val undoLabel = stringResource(R.string.undo)

    // Removal is immediate; the snackbar offers a way back instead of a confirmation dialog.
    fun removeWithUndo(removed: List<FavDeals>, message: String) {
        if (removed.size == 1) viewModel.deleteAFav(removed.first()) else viewModel.deleteAllFav()
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(message, actionLabel = undoLabel, withDismissAction = false)
            if (result == SnackbarResult.ActionPerformed) viewModel.restoreFavs(removed)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    FavHeader(
                        gameCount = watchlist.size,
                        storeCount = favoriteStoreIds.size,
                        onClearAll = { removeWithUndo(watchlist, clearedMessage) }
                    )
                }
            )
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = 88.dp
                ),
            ) {

                item(key = "stores") {
                    FavoriteStores(
                        favoriteIds = favoriteStoreIds,
                        loaded = favStoreIds != null,
                        onToggle = { viewModel.markStoreAsFav(it) }
                    )
                }

                item(key = "watchlist_header") {
                    SectionHeader(
                        title = stringResource(R.string.watchlist),
                        hint = if (watchlist.isNotEmpty()) stringResource(R.string.watchlist_hint) else null,
                        modifier = Modifier.padding(top = 22.dp)
                    )
                }

                if (watchlist.isEmpty()) {
                    item(key = "empty") {
                        EmptyWatchlist(onBrowseDeals = {
                            navController.navigate(Routes.Deals) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                    }
                } else {
                    itemsIndexed(watchlist, key = { _, fav -> fav.id }) { index, fav ->
                        WatchlistRow(
                            fav = fav,
                            shape = GroupedListShapes.forIndex(index, watchlist.size),
                            onClick = { openFavDeal(navController, fav) },
                            onRemove = {
                                removeWithUndo(
                                    listOf(fav),
                                    removedMessage.format(fav.title ?: "")
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = if (index < watchlist.lastIndex) 4.dp else 0.dp)
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}

/** Same routing as the old favorites tab: favorites saved from different sources open different screens. */
private fun openFavDeal(navController: NavHostController, fav: FavDeals) {
    val isItadId = fav.dealID.firstOrNull()?.isDigit() == true
    val route = when {
        isItadId && fav.steamAppId != null -> Routes.IsThereAnyDealSteamGameDetails(gameId = fav.dealID, title = fav.title)
        isItadId -> Routes.GameInfo(gameId = fav.dealID, title = fav.title)
        fav.steamAppId != null -> Routes.SteamGameDetails(steamId = fav.steamAppId, dealId = fav.dealID, title = fav.title)
        else -> Routes.DealsLookup(dealId = fav.dealID, title = fav.title)
    }
    navController.navigate(route)
}

@Composable
private fun FavHeader(gameCount: Int, storeCount: Int, onClearAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text = pluralStringResource(R.plurals.fav_games_count, gameCount, gameCount) + " · " +
                        pluralStringResource(R.plurals.fav_stores_count, storeCount, storeCount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.favorites_title),
                style = MaterialTheme.typography.displayMediumEmphasized,
            )
        }
        if (gameCount > 0) {
            FilledTonalButton(
                onClick = onClearAll,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                contentPadding = PaddingValues(start = 10.dp, end = 14.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = stringResource(R.string.clear_all),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, hint: String?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLargeEmphasized.copy(fontFamily = MaterialTheme.typography.headlineSmall.fontFamily))
        if (hint != null) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FavoriteStores(favoriteIds: List<Int>, loaded: Boolean, onToggle: (Int) -> Unit) {
    // Favorites lead the row, but the order is fixed once loaded so tiles don't jump while toggling.
    val stores: List<Store> = remember(loaded) {
        val all = StoreUtil.getStores()
        all.filter { it.id in favoriteIds } + all.filter { it.id !in favoriteIds }
    }
    Column(modifier = Modifier.padding(top = 6.dp)) {
        SectionHeader(title = stringResource(R.string.your_stores), hint = stringResource(R.string.pinned_first_in_filters))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stores, key = { it.id }) { store ->
                StoreTile(store = store, isFavorite = store.id in favoriteIds, onClick = { onToggle(store.id) })
            }
        }
    }
}

@Composable
private fun StoreTile(store: Store, isFavorite: Boolean, onClick: () -> Unit) {
    val gift = MaterialTheme.extendedColors
    // Selection morphs the tile from a rounded square to a near-pill.
    val corner by animateDpAsState(
        targetValue = if (isFavorite) 28.dp else 16.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "storeTileCorner"
    )
    val selectedState = stringResource(if (isFavorite) R.string.favorite_store else R.string.not_favorite_store)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(corner),
        color = if (isFavorite) gift.giftContainer else MaterialTheme.colorScheme.surfaceContainer,
        contentColor = if (isFavorite) gift.onGiftContainer else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .size(width = 92.dp, height = 92.dp)
            .semantics {
                role = Role.Checkbox
                stateDescription = selectedState
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = null,
                tint = if (isFavorite) gift.giftContent else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = store.name,
                style = MaterialTheme.typography.labelLargeEmphasized,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WatchlistRow(
    fav: FavDeals,
    shape: Shape,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        onDismiss = { onRemove() },
        modifier = modifier.clip(shape),
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(end = 22.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.remove),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    ) {
        Surface(
            onClick = onClick,
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DealPoster(
                    url = fav.thumb,
                    modifier = Modifier
                        .size(width = 52.dp, height = 68.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = fav.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    priceLine(fav)?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    StatusTag(fav)
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = stringResource(R.string.remove_from_favorites, fav.title ?: ""),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun priceLine(fav: FavDeals): String? {
    val symbol = CurrencyAndCountryUtil.currencySymbolMap[fav.currencySymbol] ?: ""
    val now = fav.currentlyLowestPrice ?: return null
    val was = fav.actualPrice
    val nowText = "$symbol${CurrencyAndCountryUtil.formatAmount(now)}"
    return if (was != null && was > now) {
        stringResource(R.string.price_was, nowText, "$symbol${CurrencyAndCountryUtil.formatAmount(was)}")
    } else nowText
}

@Composable
private fun StatusTag(fav: FavDeals) {
    val cut = fav.discountPercentage?.toInt() ?: return
    val colors = MaterialTheme.extendedColors
    val (text, container, content) = when {
        cut >= 100 -> Triple(stringResource(R.string.free).uppercase(), colors.giftContainer, colors.onGiftContainer)
        cut > 0 -> Triple(stringResource(R.string.percent_off_now, cut), colors.dealContainer, colors.onDealContainer)
        else -> Triple(
            stringResource(R.string.not_on_sale),
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmallEmphasized,
        color = content,
        maxLines = 1,
        modifier = Modifier
            .background(container, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun EmptyWatchlist(onBrowseDeals: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp, top = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, BrandShapes.score),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = stringResource(R.string.empty_watchlist_title),
            style = MaterialTheme.typography.headlineSmallEmphasized,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.empty_watchlist_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onBrowseDeals, shapes = ButtonDefaults.shapes(), modifier = Modifier.height(44.dp)) {
            Text(stringResource(R.string.browse_deals), style = MaterialTheme.typography.labelLargeEmphasized)
        }
    }
}
