package com.rkbapps.gdealz.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Shapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// M3 Expressive corner scale (includes the increased / extra-extra-large steps).
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    largeIncreased = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
    extraLargeIncreased = RoundedCornerShape(32.dp),
    extraExtraLarge = RoundedCornerShape(48.dp),
)

/**
 * Grouped lists show grouping with shape: 24dp outer corners, 8dp inner corners,
 * with a 2–6dp gap between items (see [GroupedListGap]).
 */
object GroupedListShapes {
    private val outer = 24.dp
    private val inner = 8.dp

    val single = RoundedCornerShape(outer)
    val top = RoundedCornerShape(topStart = outer, topEnd = outer, bottomStart = inner, bottomEnd = inner)
    val middle = RoundedCornerShape(inner)
    val bottom = RoundedCornerShape(topStart = inner, topEnd = inner, bottomStart = outer, bottomEnd = outer)

    fun forIndex(index: Int, count: Int): Shape = when {
        count == 1 -> single
        index == 0 -> top
        index == count - 1 -> bottom
        else -> middle
    }
}

val GroupedListGap = 3.dp

/** Abstract shapes — each has one job in the app. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object BrandShapes {
    /** Discount badge (-70%). */
    val discount: Shape @Composable get() = MaterialShapes.Sunny.toShape()

    /** Giveaway / FREE badge. */
    val giveaway: Shape @Composable get() = MaterialShapes.Burst.toShape()

    /** Scores and step counters. */
    val score: Shape @Composable get() = MaterialShapes.Cookie9Sided.toShape()

    /** Placeholder art when a poster is missing. */
    val placeholder: Shape @Composable get() = MaterialShapes.Clover4Leaf.toShape()
}