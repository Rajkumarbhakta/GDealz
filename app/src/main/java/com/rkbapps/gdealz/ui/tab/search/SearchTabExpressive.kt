package com.rkbapps.gdealz.ui.tab.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.search.SearchResult
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealPoster
import com.rkbapps.gdealz.ui.tab.deals.composables.DiscountBadge
import com.rkbapps.gdealz.ui.tab.deals.composables.formatPrice
import com.rkbapps.gdealz.ui.tab.deals.composables.priceScaleFor
import com.rkbapps.gdealz.ui.theme.BrandShapes
import com.rkbapps.gdealz.ui.theme.GroupedListGap
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil

private val ListSidePadding = 12.dp

private enum class RefineChip { ON_SALE, FREE_NOW, UNDER_PRICE, HISTORICAL_LOW }

private val Deals.isFree get() = cut == 100 || price?.amount == 0.0

private fun Deals.matches(chip: RefineChip, underPrice: Int): Boolean = when (chip) {
    RefineChip.ON_SALE -> (cut ?: 0) > 0
    RefineChip.FREE_NOW -> isFree
    RefineChip.UNDER_PRICE -> (price?.amount ?: Double.MAX_VALUE) < underPrice
    // Same flags the deal cards label: N = new low, H = historical low.
    RefineChip.HISTORICAL_LOW -> flag == "N" || flag == "H"
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchTabExpressive(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.isThereAnyDealSearchResult.collectAsStateWithLifecycle()
    val searchPrices by viewModel.searchPrices.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

    val keyboard = LocalSoftwareKeyboardController.current
    var refineChips by rememberSaveable { mutableStateOf(setOf<RefineChip>()) }

    val search: (String) -> Unit = { query ->
        keyboard?.hide()
        viewModel.searchWithPrices(query)
    }

    val results = searchResult.data
    val prices = searchPrices.data

    val currency = remember(prices) { prices?.values?.firstNotNullOfOrNull { it.price?.currency } }
    val underPrice = remember(currency) { priceScaleFor(currency).presets[1] }

    // The text stays editable after a search, so highlight what was actually searched for.
    val highlight = remember(results) { searchQuery.trim() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        SearchField(
            query = searchQuery,
            onQueryChange = {
                if (it.isEmpty()) viewModel.clearSearch() else viewModel.updateSearchQuery(it)
            },
            onSearch = { search(searchQuery) },
            onClear = viewModel::clearSearch,
        )

        when {
            searchResult.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
            }

            searchResult.error != null -> ErrorScreen(searchResult.error ?: stringResource(R.string.error_occurred))

            results != null -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(GroupedListGap)
            ) {
                item(key = "match_count") {
                    Text(
                        text = pluralStringResource(
                            R.plurals.games_match,
                            results.size,
                            results.size
                        ),
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 8.dp)
                    )
                }
                if (results.isEmpty()) {
                    item(key = "no_matches") {
                        Text(
                            text = stringResource(R.string.no_games_match_filters),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
                itemsIndexed(results, key = { _, game -> game.id }) { index, game ->
                    SearchResultItem(
                        game = game,
                        highlight = highlight,
                        deal = prices?.get(game.id),
                        pricesLoading = searchPrices.isLoading,
                        pricesKnown = prices != null,
                        shape = GroupedListShapes.forIndex(index, results.size),
                        modifier = Modifier.padding(horizontal = ListSidePadding),
                        onClick = {
                            navController.navigate(
                                Routes.IsThereAnyDealSteamGameDetails(gameId = game.id, title = game.title)
                            )
                        }
                    )
                }
                if (recentSearches.isNotEmpty()) {
                    item(key = "recent") {
                        RecentSearches(
                            searches = recentSearches,
                            onSelect = search,
                            onClear = viewModel::clearRecentSearches,
                        )
                    }
                }
            }

            else -> Column(modifier = Modifier.fillMaxSize()) {
                if (recentSearches.isNotEmpty()) {
                    RecentSearches(
                        searches = recentSearches,
                        onSelect = search,
                        onClear = viewModel::clearRecentSearches,
                    )
                }
                SearchPrompt(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
        textStyle = MaterialTheme.typography.bodyLargeEmphasized.copy(fontSize = 17.sp),
        placeholder = { Text(stringResource(R.string.search_pc_games)) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        } else null,
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    )
}


/** One row of the grouped results. [shape] comes from GroupedListShapes for its position. */
@Composable
private fun SearchResultItem(
    game: SearchResult,
    highlight: String,
    deal: Deals?,
    pricesLoading: Boolean,
    pricesKnown: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DealPoster(
                url = game.assets?.boxart,
                modifier = Modifier
                    .size(width = 48.dp, height = 64.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = highlightedTitle(game.title.orEmpty(), highlight),
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                when {
                    pricesLoading -> Box(
                        Modifier
                            .padding(vertical = 3.dp)
                            .size(width = 120.dp, height = 12.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.extraSmall)
                    )

                    deal != null -> Text(
                        text = deal.priceLine(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            when {
                (deal?.cut ?: 0) > 0 -> DiscountBadge(
                    cut = deal?.cut ?: 0,
                    size = 48.dp,
                    textStyle = PriceTextStyles.small.copy(fontSize = 13.sp),
                )

                pricesKnown -> Text(
                    text = stringResource(R.string.no_deal_yet),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(56.dp)
                )
            }
        }
    }
}

@Composable
private fun Deals.priceLine(): String {
    val store = shop?.name.orEmpty()
    val current = if (isFree) stringResource(R.string.free) else CurrencyAndCountryUtil.getCurrencyAndAmount(price)
    return if ((cut ?: 0) > 0) {
        stringResource(
            R.string.price_was,
            stringResource(R.string.price_on_store, current, store),
            CurrencyAndCountryUtil.getCurrencyAndAmount(regular)
        )
    } else {
        stringResource(R.string.cheapest_on_store, current, store)
    }
}

@Composable
private fun highlightedTitle(title: String, query: String): AnnotatedString {
    val start = if (query.isEmpty()) -1 else title.indexOf(query, ignoreCase = true)
    if (start < 0) return AnnotatedString(title)
    val matchStyle = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
    return buildAnnotatedString {
        append(title.substring(0, start))
        withStyle(matchStyle) { append(title.substring(start, start + query.length)) }
        append(title.substring(start + query.length))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentSearches(
    searches: List<String>,
    onSelect: (String) -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.recent),
                style = MaterialTheme.typography.titleSmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(
                onClick = onClear,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.clear), style = MaterialTheme.typography.labelLargeEmphasized)
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            searches.forEach { search ->
                AssistChip(
                    onClick = { onSelect(search) },
                    label = { Text(search) },
                    leadingIcon = {
                        Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    shape = CircleShape,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    border = null,
                )
            }
        }
    }
}

@Composable
private fun SearchPrompt(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, BrandShapes.score),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = stringResource(R.string.try_searching),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
