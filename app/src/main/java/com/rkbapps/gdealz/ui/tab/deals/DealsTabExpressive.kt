package com.rkbapps.gdealz.ui.tab.deals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.models.deal.DealItem
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealGridCard
import com.rkbapps.gdealz.ui.tab.deals.composables.DealListItem
import com.rkbapps.gdealz.ui.tab.deals.composables.DiscountBadge
import com.rkbapps.gdealz.ui.tab.deals.composables.FilterBottomSheetExpressive
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.ui.theme.GroupedListGap
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.Country
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.Store
import com.rkbapps.gdealz.util.StoreUtil
import kotlinx.coroutines.launch

private val ListSidePadding = 12.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DealsTabExpressive(
    navController: NavHostController,
    viewModel: DealsTabViewModel = hiltViewModel()
) {
    val filter by viewModel.isThereAnyDealFilter.collectAsStateWithLifecycle()
    val country by viewModel.country.collectAsStateWithLifecycle()
    val isCountryUnset by viewModel.isCountryUnset.collectAsStateWithLifecycle()
    val favStoreIds by viewModel.favStoreIds.collectAsStateWithLifecycle()
    val bigDropDeals by viewModel.bigDropDeals.collectAsStateWithLifecycle()
    val deals = viewModel.isThereAnyDeals.collectAsLazyPagingItems()

    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var isGrid by rememberSaveable { mutableStateOf(false) }
    var currency by remember { mutableStateOf<String?>(null) }

    val defaultFilter = remember { IsThereAnyDealFilters() }

    LaunchedEffect(isCountryUnset) {
        if (isCountryUnset) showCountryDialog = true
    }

    val openDetails: (DealItem) -> Unit = { deal ->
        if (deal.deal != null) {
            navController.navigate(
                Routes.IsThereAnyDealSteamGameDetails(gameId = deal.id, title = deal.title)
            )
        }
    }

    val toolbarScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = FloatingToolbarExitDirection.Bottom)

    if (showCountryDialog) {
        // A country is mandatory on first launch, so the dialog only dismisses once one is set.
        Dialog(onDismissRequest = { if (country != null) showCountryDialog = false }) {
            ChooseCountryDialog(
                modifier = Modifier.height(500.dp),
                selectedCountry = Country.entries.find { it.key == country } ?: Country.US
            ) {
                viewModel.updateCountry(it.key)
                showCountryDialog = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    DealsHeader(
                        countryName = Country.entries.find { it.key == country }?.value,
                        currency = currency,
                        onChangeRegion = { showCountryDialog = true }
                    )
                }
            )
        }
    ) { innerPadding->


        if (showFilterSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding()),
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
            ) {
                val closeSheet = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showFilterSheet = false
                    }
                }
                FilterBottomSheetExpressive(
                    appliedFilters = filter,
                    favStoreIds = favStoreIds?.ids,
                    currency = currency,
                    onCancel = { closeSheet() },
                ) {
                    viewModel.updateIsThereAnyDealFilter(it)
                    closeSheet()
                }
            }
        }




        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                )
        ) {
            val snapshot = deals.itemSnapshotList
            currency = snapshot.items.firstOrNull()?.deal?.regular?.currency

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isGrid) 2 else 1),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(toolbarScrollBehavior),
                contentPadding = PaddingValues(
                    // Room for the floating toolbar so the last item can scroll clear of it.
                    bottom = 104.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(if (isGrid) 8.dp else GroupedListGap),
            ) {

                if (!bigDropDeals.data.isNullOrEmpty()) {
                    fullWidthItem(key = "biggest_drops") {
                        BiggestDropsCarousel(deals = bigDropDeals.data!!, onClick = openDetails)
                    }
                }

                fullWidthItem(key = "stores") {
                    StoreChips(
                        allStoreIds = StoreUtil.getStoreIds(),
                        selectedIds = filter.stores,
                        favoriteIds = favStoreIds?.ids.orEmpty(),
                        onSelectAll = { viewModel.updateIsThereAnyDealFilter(filter.copy(stores = emptyList())) },
                        onToggle = { id ->
                            val stores =
                                if (id in filter.stores) filter.stores - id else filter.stores + id
                            viewModel.updateIsThereAnyDealFilter(filter.copy(stores = stores))
                        }
                    )
                }

                fullWidthItem(key = "section_header") {
                    SectionHeader(
                        showClear = filter != defaultFilter,
                        onClear = { viewModel.clearIsThereAnyDealFilter() }
                    )
                }

                dealItems(deals = deals, isGrid = isGrid, onClick = openDetails)
            }

            AnimatedVisibility(
                visible = !deals.loadState.hasError,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    scrollBehavior = toolbarScrollBehavior,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    FiltersButton(
                        activeCount = filter.activeFilterCount(),
                        onClick = { showFilterSheet = true }
                    )
                    SortMenuButton(
                        selected = filter.sort,
                        onSelect = { viewModel.updateIsThereAnyDealFilter(filter.copy(sort = it)) }
                    )
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(
                            imageVector = if (isGrid) Icons.Rounded.ViewAgenda else Icons.Rounded.GridView,
                            contentDescription = stringResource(if (isGrid) R.string.show_as_list else R.string.show_as_grid)
                        )
                    }
                }
            }
        }
    }
}

private fun IsThereAnyDealFilters.activeFilterCount(): Int =
    listOf(stores.isNotEmpty(), lowerPrice != null || upperPrice != null, discount != null || maxDiscount != null).count { it }

private fun LazyGridScope.fullWidthItem(key: String, content: @Composable () -> Unit) =
    item(key = key, span = { GridItemSpan(maxLineSpan) }) { content() }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyGridScope.dealItems(
    deals: LazyPagingItems<DealItem>,
    isGrid: Boolean,
    onClick: (DealItem) -> Unit,
) {
    when (val refresh = deals.loadState.refresh) {
        is LoadState.Loading -> fullWidthItem(key = "loading") {
            Box(Modifier
                .fillMaxWidth()
                .padding(48.dp), contentAlignment = Alignment.Center) {
                ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
            }
        }

        is LoadState.Error -> fullWidthItem(key = "error") {
            ErrorScreen(refresh.error.message ?: stringResource(R.string.error_occurred))
        }

        is LoadState.NotLoading -> if (deals.itemCount == 0) {
            fullWidthItem(key = "empty") { ErrorScreen(stringResource(R.string.no_deals_found)) }
        }
    }

    items(count = deals.itemCount) { index ->
        val deal = deals[index] ?: return@items
        if (isGrid) {
            // Grid lines start right after full-width headers, so even indices are the left column.
            val isLeft = index % 2 == 0
            DealGridCard(
                deal = deal,
                modifier = Modifier.padding(
                    start = if (isLeft) ListSidePadding else 0.dp,
                    end = if (isLeft) 0.dp else ListSidePadding
                ),
                onClick = { onClick(deal) }
            )
        } else {
            DealListItem(
                deal = deal,
                shape = GroupedListShapes.forIndex(index, deals.itemCount),
                modifier = Modifier.padding(horizontal = ListSidePadding),
                onClick = { onClick(deal) }
            )
        }
    }

    when (deals.loadState.append) {
        is LoadState.Loading -> fullWidthItem(key = "append_loading") {
            Box(Modifier
                .fillMaxWidth()
                .padding(16.dp), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        }

        is LoadState.Error -> fullWidthItem(key = "append_error") {
            Text(
                text = stringResource(R.string.error_occurred),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }

        is LoadState.NotLoading -> {}
    }
}

@Composable
private fun DealsHeader(
    countryName: String?,
    currency: String?,
    onChangeRegion: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val region = listOfNotNull(
                countryName,
                currency?.let { "${CurrencyAndCountryUtil.currencySymbolMap[it] ?: ""} $it".trim() }
            ).joinToString(" · ")
            if (region.isNotEmpty()) {
                Text(
                    text = region,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = stringResource(R.string.tab_deals),
                style = MaterialTheme.typography.displayMediumEmphasized,
            )
        }
        FilledIconButton(
            onClick = onChangeRegion,
            shapes = IconButtonShapes(
                shape = IconButtonDefaults.mediumSquareShape,
                pressedShape = IconButtonDefaults.mediumPressedShape
            ),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(bottom = 4.dp)
                .size(48.dp)
        ) {
            Icon(Icons.Rounded.Public, contentDescription = stringResource(R.string.choose_country))
        }
    }
}

@Composable
private fun BiggestDropsCarousel(deals: List<DealItem>, onClick: (DealItem) -> Unit) {
    Column() {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.dealContent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.biggest_drops_today),
                style = MaterialTheme.typography.titleSmallEmphasized
            )
        }
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { deals.size },
            preferredItemWidth = 250.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(188.dp)
        ) { index ->
            val deal = deals[index]
            val info = carouselItemDrawInfo
            // 0 when the item is squeezed to its smallest size, 1 when fully open.
            val openFraction = if (info.maxSize > info.minSize) {
                ((info.size - info.minSize) / (info.maxSize - info.minSize)).coerceIn(0f, 1f)
            } else 1f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .maskClip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onClick(deal) }
            ) {
                AsyncImage(
                    model = deal.assets?.banner400 ?: deal.assets?.banner300 ?: deal.assets?.boxart,
                    contentDescription = deal.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = openFraction }
                        .background(
                            Brush.verticalGradient(
                                0.35f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.8f)
                            )
                        )
                ) {
                    DiscountBadge(
                        cut = deal.deal?.cut ?: 0,
                        size = 64.dp,
                        textStyle = PriceTextStyles.small.copy(fontSize = 17.sp),
                        modifier = Modifier.padding(14.dp)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        // Text sits on a dark scrim over artwork, so it is white in both themes.
                        Text(
                            text = deal.title ?: "",
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = listOfNotNull(
                                CurrencyAndCountryUtil.getCurrencyAndAmount(deal.deal?.price),
                                deal.deal?.shop?.name
                            ).joinToString(" · "),
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreChips(
    allStoreIds: List<Int>,
    selectedIds: List<Int>,
    favoriteIds: List<Int>,
    onSelectAll: () -> Unit,
    onToggle: (Int) -> Unit,
) {
    // Selected stores first (they may come from the filter sheet), then favorites, then popular stores.
    val stores: List<Store> = remember(selectedIds, favoriteIds) {
        (selectedIds + favoriteIds + allStoreIds).distinct().mapNotNull { StoreUtil.getStore(it) }
    }
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    val checkIcon: @Composable () -> Unit = {
        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 18.dp, bottom = 6.dp)
    ) {
        item(key = "all") {
            val selected = selectedIds.isEmpty()
            FilterChip(
                selected = selected,
                onClick = onSelectAll,
                label = { Text(stringResource(R.string.all_stores)) },
                leadingIcon = if (selected) checkIcon else null,
                shape = MaterialTheme.shapes.medium,
                colors = chipColors,
            )
        }
        items(stores, key = { it.id }) { store ->
            val selected = store.id in selectedIds
            FilterChip(
                selected = selected,
                onClick = { onToggle(store.id) },
                label = { Text(store.name) },
                leadingIcon = when {
                    selected -> checkIcon
                    store.id in favoriteIds -> {
                        {
                            Icon(
                                Icons.Rounded.Star,
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.giftContent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    else -> null
                },
                shape = MaterialTheme.shapes.medium,
                colors = chipColors,
            )
        }
    }
}

@Composable
private fun SectionHeader(showClear: Boolean, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 4.dp)
            .sizeIn(minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.latest_deals),
            style = MaterialTheme.typography.headlineSmallEmphasized
        )
        if (showClear) {
            TextButton(
                onClick = onClear,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.clear_filter))
            }
        }
    }
}

@Composable
private fun FiltersButton(activeCount: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
        contentPadding = PaddingValues(start = 14.dp, end = 18.dp),
        modifier = Modifier.height(48.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.filter),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(R.string.filters),
            style = MaterialTheme.typography.labelLargeEmphasized,
            modifier = Modifier.padding(start = 8.dp)
        )
        if (activeCount > 0) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .sizeIn(minWidth = 20.dp, minHeight = 20.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCount.toString(),
                    style = MaterialTheme.typography.labelSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SortMenuButton(
    selected: IsThereAnyDealSortingOptions,
    onSelect: (IsThereAnyDealSortingOptions) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = selected.value,
                style = MaterialTheme.typography.labelLargeEmphasized,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.sizeIn(maxWidth = 140.dp)
            )
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.sort_by),
                modifier = Modifier.size(18.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.large,
        ) {
            IsThereAnyDealSortingOptions.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    trailingIcon = if (option == selected) {
                        { Icon(Icons.Rounded.Check, contentDescription = null) }
                    } else null,
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DealsHeaderPreview() {
    GDealzTheme(dynamicColor = false) {
        DealsHeader(countryName = "India", currency = "INR", onChangeRegion = {})
    }
}
