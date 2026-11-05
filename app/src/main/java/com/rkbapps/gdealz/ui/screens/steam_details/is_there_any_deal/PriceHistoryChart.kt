package com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.price.PriceHistoryEntry
import com.rkbapps.gdealz.ui.theme.extendedColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class PricePoint(val time: Long, val amount: Double)

fun parseIsoDate(value: String?): Date? = try {
    value?.let { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(it) }
} catch (_: Exception) {
    null
}

/**
 * Turns the per-store change log into the cheapest price across all stores over time.
 * The log only lists changes, so a store missing from it has held today's price all along.
 */
fun lowestPriceSeries(history: List<PriceHistoryEntry>, currentDeals: List<Deals>, now: Long): List<PricePoint> {
    // Some stores log in their own currency (USD, EUR...), which can't be compared to local prices.
    val currency = currentDeals.firstNotNullOfOrNull { it.price?.currency }
    val events = history
        .filter { it.deal == null || it.deal.price?.currency == currency }
        .mapNotNull { entry -> parseIsoDate(entry.timestamp)?.let { it.time to entry } }
        .sortedBy { it.first }
    val shopsInLog = events.mapNotNull { it.second.shop?.id }.toSet()

    val shopPrices = HashMap<Int, Double>()
    currentDeals.forEach { deal ->
        val shopId = deal.shop?.id ?: return@forEach
        val amount = deal.price?.amount ?: return@forEach
        if (shopId !in shopsInLog) shopPrices[shopId] = amount
    }

    val points = mutableListOf<PricePoint>()
    events.forEach { (time, entry) ->
        val shopId = entry.shop?.id ?: return@forEach
        val amount = entry.deal?.price?.amount
        if (amount != null) shopPrices[shopId] = amount else shopPrices.remove(shopId)
        val lowest = shopPrices.values.minOrNull() ?: return@forEach
        if (points.lastOrNull()?.amount != lowest) points.add(PricePoint(time, lowest))
    }
    currentDeals.mapNotNull { it.price?.amount }.minOrNull()?.let { points.add(PricePoint(now, it)) }
    return points
}

/** Step chart: prices hold until they change, so a smooth line would invent values. */
@Composable
fun PriceHistoryChart(points: List<PricePoint>, lowestEver: Double?, end: Long, modifier: Modifier = Modifier) {
    val start = end - TimeUnit.DAYS.toMillis(365)
    val line = MaterialTheme.colorScheme.primary
    val low = MaterialTheme.extendedColors.dealContent
    val dotBorder = MaterialTheme.colorScheme.surfaceContainer
    val description = stringResource(R.string.price_chart_description)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
                .semantics { contentDescription = description }
        ) {
            val amounts = points.map { it.amount } + listOfNotNull(lowestEver)
            val minAmount = amounts.min()
            val range = (amounts.max() - minAmount).takeIf { it > 0 } ?: 1.0
            val inset = 8.dp.toPx()
            val x = { time: Long -> ((time - start).toFloat() / (end - start)).coerceIn(0f, 1f) * size.width }
            val y = { amount: Double ->
                inset + ((amounts.max() - amount) / range).toFloat() * (size.height - inset * 2)
            }

            var lastX = 0f
            val path = Path().apply {
                moveTo(0f, y(points.first().amount))
                points.forEach { point ->
                    // Horizontal first (the old price holds), then the step to the new one.
                    relativeLineTo(x(point.time) - lastX, 0f)
                    lastX = x(point.time)
                    lineTo(lastX, y(point.amount))
                }
                lineTo(size.width, y(points.last().amount))
            }
            val area = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(area, line.copy(alpha = 0.14f))
            lowestEver?.let {
                drawLine(
                    color = low,
                    start = Offset(0f, y(it)),
                    end = Offset(size.width, y(it)),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                )
            }
            drawPath(path, line, style = Stroke(width = 2.5.dp.toPx(), join = StrokeJoin.Round))
            val dot = Offset(size.width - 7.dp.toPx(), y(points.last().amount))
            drawCircle(dotBorder, radius = 7.dp.toPx(), center = dot)
            drawCircle(line, radius = 5.dp.toPx(), center = dot)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val format = remember { SimpleDateFormat("MMM ''yy", Locale.getDefault()) }
            listOf(
                format.format(Date(start)),
                format.format(Date((start + end) / 2)),
                stringResource(R.string.now_label)
            ).forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
