package com.rkbapps.gdealz.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.paging.LOG_TAG
import com.rkbapps.gdealz.models.Giveaway
import java.time.LocalTime


import java.text.SimpleDateFormat
import java.util.*

fun getStatusFromEndDate(endDateStr: String): Boolean? {
    Log.e("STATUS ",endDateStr)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    return try {
        val endDate = dateFormat.parse(endDateStr)
        val currentDate = Date()

        if (endDate != null && endDate.after(currentDate)) {
            // Active
            true
        } else {
            // Expired
            false
        }
    } catch (e: Exception) {
        null
    }
}


/**
 * GamerPower sends the platforms of a giveaway as one comma separated string,
 * e.g. "PC, Steam, DRM-Free", so it has to be split before it can be matched or listed.
 */
fun String.toPlatformList(): List<String> =
    split(",").map { it.trim() }.filter { it.isNotEmpty() }


/**
 * Whether a giveaway passes a store/type selection.
 *
 * An empty list means "no restriction" on that axis, matching the Free Deals tab's filter.
 * Shared so the tab and the notification worker cannot drift apart.
 */
fun Giveaway.matchesFilter(platforms: List<String>, types: List<String>): Boolean {
    val matchesPlatform = platforms.isEmpty() || this.platforms.toPlatformList().any { it in platforms }
    val matchesType = types.isEmpty() || this.type in types
    return matchesPlatform && matchesType
}

/**
 * Parses GamerPower's `worth` field, e.g. "$29.99" or "$1,299.00", into a plain amount.
 *
 * Returns null when there is no price to compare - most commonly the literal "N/A", which a large
 * share of giveaways carry. Callers must treat null as passing any threshold, otherwise setting a
 * minimum worth would silently suppress most notifications.
 */
fun String.toWorthAmount(): Double? =
    replace(Regex("[^0-9.]"), "").takeIf { it.isNotBlank() }?.toDoubleOrNull()

/**
 * Whether a giveaway clears a minimum-worth threshold.
 *
 * A giveaway with no listed value always passes: GamerPower reports "N/A" for a large share of
 * them, so treating that as zero would make even a $1 threshold suppress most notifications.
 */
fun Giveaway.meetsMinWorth(minWorth: Int): Boolean {
    if (minWorth <= 0) return true
    val amount = worth.toWorthAmount() ?: return true
    return amount >= minWorth
}

/**
 * Whether [nowHour] falls inside a quiet-hours window.
 *
 * Handles windows that wrap past midnight (22 -> 8). The window is start-inclusive and
 * end-exclusive; an empty window (start == end) is treated as "never quiet".
 */
fun isWithinQuietHours(start: Int, end: Int, nowHour: Int = LocalTime.now().hour): Boolean = when {
    start == end -> false
    start < end -> nowHour in start until end
    else -> nowHour !in end..<start
}


fun Context.hasNetwork(): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}


fun calculatePercentage(actualPrice: String, discountPrice: String): Int {
    return try {
        val normalPrice = if (discountPrice.isNotEmpty())actualPrice.toDouble()else 0.0
        val salePrice = if (discountPrice.isNotEmpty())discountPrice.toDouble()else 0.0
        val discount = ((salePrice * 100) / normalPrice)
        (100-discount).toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue:Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "Shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Restart
            ), label = "ShimmerAnimation"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent,Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

