package com.rkbapps.gdealz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rkbapps.gdealz.R

// Both fonts are variable; each weight is a separate instance of the same file.
// (On API < 26 variation settings are ignored and the default instance is used.)
@OptIn(ExperimentalTextApi::class)
private fun bricolage(weight: Int, width: Float) = Font(
    R.font.bricolage_grotesque,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.width(width),
    )
)

@OptIn(ExperimentalTextApi::class)
private fun dmSans(weight: Int) = Font(
    R.font.dm_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

/** Bricolage Grotesque at 85% width — large display titles ("Deals", page heroes). */
val DisplayFontFamily = FontFamily(
    bricolage(600, 85f),
    bricolage(700, 85f),
    bricolage(800, 85f),
)

/** Bricolage Grotesque at normal width — headlines, section titles, prices. */
val HeadlineFontFamily = FontFamily(
    bricolage(400, 100f),
    bricolage(500, 100f),
    bricolage(600, 100f),
    bricolage(700, 100f),
    bricolage(800, 100f),
)

/** DM Sans — titles, body and labels. */
val BodyFontFamily = FontFamily(
    dmSans(400),
    dmSans(500),
    dmSans(600),
    dmSans(700),
)

// Default Material 3 typography values
val baseline = Typography()

private fun TextStyle.display(weight: Int) =
    copy(fontFamily = DisplayFontFamily, fontWeight = FontWeight(weight), letterSpacing = (-0.5).sp)

private fun TextStyle.headline(weight: Int) =
    copy(fontFamily = HeadlineFontFamily, fontWeight = FontWeight(weight))

private fun TextStyle.body(weight: Int) =
    copy(fontFamily = BodyFontFamily, fontWeight = FontWeight(weight))

// Emphasized styles sit one weight step above their baseline, per M3 Expressive.
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.display(700),
    displayMedium = baseline.displayMedium.copy(fontSize = 44.sp, lineHeight = 48.sp).display(700),
    displaySmall = baseline.displaySmall.display(700),
    headlineLarge = baseline.headlineLarge.headline(600),
    headlineMedium = baseline.headlineMedium.headline(600),
    headlineSmall = baseline.headlineSmall.copy(fontSize = 20.sp, lineHeight = 28.sp).headline(600),
    titleLarge = baseline.titleLarge.body(600),
    titleMedium = baseline.titleMedium.body(600),
    titleSmall = baseline.titleSmall.body(600),
    bodyLarge = baseline.bodyLarge.body(400),
    bodyMedium = baseline.bodyMedium.body(400),
    bodySmall = baseline.bodySmall.body(400),
    labelLarge = baseline.labelLarge.body(500),
    labelMedium = baseline.labelMedium.body(500),
    labelSmall = baseline.labelSmall.body(500),

    displayLargeEmphasized = baseline.displayLarge.display(800),
    displayMediumEmphasized = baseline.displayMedium.copy(fontSize = 44.sp, lineHeight = 48.sp).display(800),
    displaySmallEmphasized = baseline.displaySmall.display(800),
    headlineLargeEmphasized = baseline.headlineLarge.headline(700),
    headlineMediumEmphasized = baseline.headlineMedium.headline(700),
    headlineSmallEmphasized = baseline.headlineSmall.copy(fontSize = 20.sp, lineHeight = 28.sp).headline(700),
    titleLargeEmphasized = baseline.titleLarge.body(700),
    titleMediumEmphasized = baseline.titleMedium.body(700),
    titleSmallEmphasized = baseline.titleSmall.body(700),
    bodyLargeEmphasized = baseline.bodyLarge.body(500),
    bodyMediumEmphasized = baseline.bodyMedium.body(500),
    bodySmallEmphasized = baseline.bodySmall.body(500),
    labelLargeEmphasized = baseline.labelLarge.body(700),
    labelMediumEmphasized = baseline.labelMedium.body(700),
    labelSmallEmphasized = baseline.labelSmall.body(700),
)

/** Prices are the main takeaway on every deal, so they get their own heavy Bricolage styles. */
object PriceTextStyles {
    val large = TextStyle(fontFamily = HeadlineFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 32.sp)
    val medium = TextStyle(fontFamily = HeadlineFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 24.sp)
    val small = TextStyle(fontFamily = HeadlineFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 20.sp)
}