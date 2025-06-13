package com.rkbapps.gdealz.util

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

