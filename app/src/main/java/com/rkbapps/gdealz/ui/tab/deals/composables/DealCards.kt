package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.deal.DealItem
import com.rkbapps.gdealz.models.deal.Price
import com.rkbapps.gdealz.ui.theme.BrandShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil

private val DealItem.cut get() = deal?.cut ?: 0
private val DealItem.isFree get() = cut == 100

/** Uses the regular price's currency for both prices, matching the rest of the app. */
private fun DealItem.formatPrice(price: Price?): String =
    "${CurrencyAndCountryUtil.currencySymbolMap[deal?.regular?.currency] ?: "-"}${CurrencyAndCountryUtil.formatAmount(price?.amount)}"

@Composable
private fun DealItem.currentPriceText(): String =
    if (isFree) stringResource(R.string.free) else formatPrice(deal?.price)

/** IsThereAnyDeal flags a deal as a new low (N), historical low (H) or store low (S). */
@Composable
private fun DealItem.lowLabel(): String? = when (deal?.flag) {
    "N" -> stringResource(R.string.new_low)
    "H" -> stringResource(R.string.historical_low)
    "S" -> stringResource(R.string.store_low)
    else -> null
}

/**
 * Discount badge: lime Sunny for a price cut, gold Burst for a giveaway.
 * The shape alone tells the two apart, so it never relies on color.
 */
@Composable
fun DiscountBadge(
    cut: Int,
    modifier: Modifier = Modifier,
    size: Dp = 58.dp,
    textStyle: TextStyle = PriceTextStyles.small.copy(fontSize = 15.sp),
) {
    val isFree = cut == 100
    val colors = MaterialTheme.extendedColors
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = if (isFree) colors.giftFill else colors.dealFill,
                shape = if (isFree) BrandShapes.giveaway else BrandShapes.discount
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isFree) stringResource(R.string.free).uppercase() else "-$cut%",
            style = textStyle,
            color = if (isFree) colors.onGiftFill else colors.onDealFill,
            maxLines = 1,
        )
    }
}

/** Box art with a clover placeholder while loading or when the store has no art. */
@Composable
fun DealPoster(url: String?, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { PosterPlaceholder() },
        error = { PosterPlaceholder() },
    )
}

@Composable
private fun PosterPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = (-10).dp, y = 26.dp)
                .scale(1.3f)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), BrandShapes.placeholder)
        )
    }
}

@Composable
private fun PriceRow(deal: DealItem, priceStyle: TextStyle) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = deal.currentPriceText(),
            style = priceStyle,
            color = if (deal.isFree) MaterialTheme.extendedColors.giftContent else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        if (deal.cut > 0) {
            Text(
                text = deal.formatPrice(deal.deal?.regular),
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

/** One row of the grouped deal list. [shape] comes from GroupedListShapes for its position. */
@Composable
fun DealListItem(
    deal: DealItem,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DealPoster(
                url = deal.assets?.boxart,
                modifier = Modifier
                    .size(width = 64.dp, height = 84.dp)
                    .clip(MaterialTheme.shapes.large)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = deal.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StoreLine(deal)
                PriceRow(deal, PriceTextStyles.medium)
            }
            DiscountBadge(cut = deal.cut)
        }
    }
}

@Composable
private fun StoreLine(deal: DealItem) {
    val lowLabel = deal.lowLabel()
    Row {
        Text(
            text = deal.deal?.shop?.name ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (lowLabel != null) {
            Text(
                text = " · $lowLabel",
                style = MaterialTheme.typography.bodySmallEmphasized,
                color = MaterialTheme.extendedColors.dealContent,
                maxLines = 1,
            )
        }
    }
}

/** Two-column grid variant: poster first, badge overlapping its corner. */
@Composable
fun DealGridCard(
    deal: DealItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                DealPoster(
                    url = deal.assets?.boxart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(MaterialTheme.shapes.largeIncreased)
                )
                DiscountBadge(
                    cut = deal.cut,
                    size = 52.dp,
                    textStyle = PriceTextStyles.small.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = deal.title ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StoreLine(deal)
                PriceRow(deal, PriceTextStyles.small)
            }
        }
    }
}