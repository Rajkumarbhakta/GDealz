package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.StoreUtil
import java.util.Locale
import kotlin.math.roundToInt

/** The four sorts that get the connected button group; the rest sit underneath as chips. */
private val PrimarySorts = listOf(
    IsThereAnyDealSortingOptions.TRENDING to R.string.sort_trending,
    IsThereAnyDealSortingOptions.NEWEST to R.string.sort_newest,
    IsThereAnyDealSortingOptions.LOWEST_PRICE to R.string.sort_price,
    IsThereAnyDealSortingOptions.HIGHEST_PRICE_CUT to R.string.sort_discount,
)

/** Non-favorite, non-selected stores shown before "Show all". */
private const val CollapsedStoreCount = 8

private const val DiscountStep = 5

/**
 * Price presets and slider ceiling in the user's currency. Prices from the API are in local
 * currency, so a single USD-style range would be useless in INR or JPY. Values are rough and
 * rounded to amounts a shopper would actually type.
 */
private data class PriceScale(val presets: List<Int>, val max: Int, val step: Int)

private fun priceScaleFor(currency: String?): PriceScale = when (currency) {
    "INR" -> PriceScale(listOf(200, 500, 1000), max = 3000, step = 50)
    "JPY" -> PriceScale(listOf(500, 1000, 2000), max = 8000, step = 100)
    "KRW" -> PriceScale(listOf(5000, 10000, 20000), max = 80000, step = 1000)
    "RUB" -> PriceScale(listOf(300, 600, 1200), max = 5000, step = 50)
    "UAH", "TRY" -> PriceScale(listOf(100, 250, 500), max = 2000, step = 25)
    "CNY", "HKD" -> PriceScale(listOf(30, 60, 120), max = 400, step = 5)
    "SEK", "NOK", "DKK" -> PriceScale(listOf(50, 100, 200), max = 600, step = 10)
    "BRL", "PLN" -> PriceScale(listOf(20, 50, 100), max = 300, step = 5)
    else -> PriceScale(listOf(5, 10, 20), max = 60, step = 1)
}

private fun formatPrice(currency: String?, amount: Int): String =
    "${CurrencyAndCountryUtil.currencySymbolMap[currency] ?: ""}${"%,d".format(Locale.getDefault(), amount)}"


@Composable
fun FilterBottomSheetExpressive(
    modifier: Modifier = Modifier,
    appliedFilters: IsThereAnyDealFilters = IsThereAnyDealFilters(),
    favStoreIds: List<Int>? = null,
    currency: String? = null,
    onCancel: () -> Unit = {},
    onApplyFilters: (IsThereAnyDealFilters) -> Unit,
) {
    val defaultFilter = remember { IsThereAnyDealFilters() }
    val priceScale = remember(currency) { priceScaleFor(currency) }

    var sort by remember { mutableStateOf(appliedFilters.sort) }
    var stores by remember { mutableStateOf(appliedFilters.stores) }
    var showAllStores by remember { mutableStateOf(false) }

    // The slider's top end means "any price".
    val priceState = rememberSliderState(
        value = (appliedFilters.upperPrice ?: priceScale.max).coerceAtMost(priceScale.max).toFloat(),
        trackRange = 0f..priceScale.max.toFloat(),
    )
    val discountState = rememberRangeSliderState(
        startValue = (appliedFilters.discount ?: 0).toFloat(),
        endValue = (appliedFilters.maxDiscount ?: 100).toFloat(),
        steps = 100 / DiscountStep - 1,
        trackRange = 0f..100f,
    )

    val maxPrice: Int? = priceState.value.let { value ->
        if (value >= priceScale.max) null
        else ((value / priceScale.step).roundToInt() * priceScale.step)
    }
    val minDiscount = discountState.startValue.roundToInt()
    val maxDiscount = discountState.endValue.roundToInt()

    val draft = appliedFilters.copy(
        sort = sort,
        stores = stores,
        lowerPrice = maxPrice?.let { 0 },
        upperPrice = maxPrice,
        discount = minDiscount.takeIf { it > 0 || maxDiscount < 100 },
        maxDiscount = maxDiscount.takeIf { it < 100 },
    )

    val favoriteIds = favStoreIds.orEmpty()
    val allStores = remember(favoriteIds) {
        val favorites = StoreUtil.filterFavStores(favoriteIds)
        favorites + StoreUtil.getStores().filter { it !in favorites }
    }
    val visibleStores = if (showAllStores) allStores else {
        val pinned = allStores.filter { it.id in favoriteIds || it.id in stores }
        pinned + (allStores - pinned.toSet()).take(CollapsedStoreCount)
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 6.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.headlineMediumEmphasized
            )
            TextButton(
                enabled = draft != defaultFilter,
                onClick = {
                    sort = defaultFilter.sort
                    stores = emptyList()
                    priceState.value = priceScale.max.toFloat()
                    discountState.startValue = 0f
                    discountState.endValue = 100f
                }
            ) {
                Text(stringResource(R.string.reset_all), style = MaterialTheme.typography.labelLargeEmphasized)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel(stringResource(R.string.sort_by))
            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                PrimarySorts.forEachIndexed { index, (option, label) ->
                    ToggleButton(
                        checked = sort == option,
                        onCheckedChange = { sort = option },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            PrimarySorts.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        colors = ToggleButtonDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .semantics { role = Role.RadioButton }
                    ) {
                        Text(stringResource(label), style = MaterialTheme.typography.labelLargeEmphasized, maxLines = 1)
                    }
                }
            }
            // Less common sorts stay reachable without crowding the group.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IsThereAnyDealSortingOptions.entries
                    .filter { option -> PrimarySorts.none { it.first == option } }
                    .forEach { option ->
                        FilterChip(
                            selected = sort == option,
                            onClick = { sort = option },
                            label = { Text(option.value) },
                            shape = MaterialTheme.shapes.small,
                            colors = selectedChipColors(),
                        )
                    }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionLabel(stringResource(R.string.stores_label), Modifier.padding(bottom = 0.dp))
                if (favoriteIds.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.favorites_pinned_first),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .animateContentSize(MaterialTheme.motionScheme.defaultSpatialSpec())
            ) {
                visibleStores.forEach { store ->
                    val selected = store.id in stores
                    FilterChip(
                        selected = selected,
                        onClick = { stores = if (selected) stores - store.id else stores + store.id },
                        label = { Text(store.name) },
                        leadingIcon = when {
                            selected -> {
                                { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            }

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
                        colors = selectedChipColors(),
                    )
                }
            }
            TextButton(onClick = { showAllStores = !showAllStores }) {
                Text(
                    if (showAllStores) stringResource(R.string.show_fewer_stores)
                    else stringResource(R.string.show_all_stores, allStores.size)
                )
            }

            ValueHeader(
                label = stringResource(R.string.max_price),
                value = maxPrice?.let { formatPrice(currency, it) } ?: stringResource(R.string.any),
            )
            Slider(
                state = priceState,
                onValueChange = { priceState.value = it },
                modifier = Modifier.padding(horizontal = 4.dp),
                track = {
                    SliderDefaults.Track(
                        sliderState = it,
                        trackCornerSize = 8.dp,
                        modifier = Modifier.height(16.dp),
                    )
                }
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                priceScale.presets.forEachIndexed { index, preset ->
                    val text = formatPrice(currency, preset)
                    FilterChip(
                        selected = maxPrice == preset,
                        onClick = { priceState.value = preset.toFloat() },
                        label = { Text(if (index == 0) stringResource(R.string.under_price, text) else text) },
                        shape = MaterialTheme.shapes.small,
                        colors = selectedChipColors(),
                    )
                }
                FilterChip(
                    selected = maxPrice == null,
                    onClick = { priceState.value = priceScale.max.toFloat() },
                    label = { Text(stringResource(R.string.any)) },
                    shape = MaterialTheme.shapes.small,
                    colors = selectedChipColors(),
                )
            }

            val dealColors = MaterialTheme.extendedColors
            ValueHeader(
                label = stringResource(R.string.filter_discount),
                value = if (minDiscount == 0 && maxDiscount == 100) stringResource(R.string.any)
                else "$minDiscount% – $maxDiscount%",
                valueColor = dealColors.dealContent,
            )
            val discountColors = SliderDefaults.colors(
                thumbColor = dealColors.dealContent,
                activeTrackColor = dealColors.dealContent,
                inactiveTrackColor = dealColors.dealContainer,
                activeTickColor = dealColors.dealContainer,
                inactiveTickColor = dealColors.dealContent,
            )
            RangeSlider(
                state = discountState,
                onValueChange = {
                    discountState.endValue = it.endInclusive
                    discountState.startValue = it.start
                },
                colors = discountColors,
                modifier = Modifier.padding(horizontal = 4.dp),
                track = {
                    SliderDefaults.Track(
                        rangeSliderState = it,
                        trackCornerSize = 8.dp,
                        colors = discountColors,
                        drawTick = { _, _ -> },
                        modifier = Modifier.height(16.dp),
                    )
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("100%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight)
            ) {
                Text(stringResource(R.string.cancel), style = MaterialTheme.typography.titleMediumEmphasized)
            }
            Button(
                onClick = { onApplyFilters(draft) },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .weight(1f)
                    .height(ButtonDefaults.MediumContainerHeight)
            ) {
                Text(stringResource(R.string.apply_filters), style = MaterialTheme.typography.titleMediumEmphasized)
            }
        }
    }
}

@Composable
private fun selectedChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
)

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier.padding(bottom = 10.dp)) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmallEmphasized,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 4.dp)
    )
}

@Composable
private fun ValueHeader(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 20.dp, bottom = 6.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmallEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = PriceTextStyles.medium, color = valueColor)
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun FilterBottomSheetExpressivePreview() {
    GDealzTheme(dynamicColor = false) {
        FilterBottomSheetExpressive(
            appliedFilters = IsThereAnyDealFilters(stores = listOf(61), discount = 50, maxDiscount = 90),
            favStoreIds = listOf(61, 16),
            currency = "INR",
        ) {}
    }
}