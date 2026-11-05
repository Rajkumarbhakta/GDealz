package com.rkbapps.gdealz.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/** Preset primary palettes for when dynamic color is off or unavailable. */
enum class AccentColor(
    val seed: Color,
    val tone10: Color,
    val tone20: Color,
    val tone30: Color,
    val tone80: Color,
    val tone90: Color,
) {
    INDIGO(
        seed = Color(0xFF5847C8),
        tone10 = Color(0xFF2A1A8A),
        tone20 = Color(0xFF2B1A86),
        tone30 = Color(0xFF4636B0),
        tone80 = Color(0xFFC9BFFF),
        tone90 = Color(0xFFE5DEFF),
    ),
    TEAL(
        seed = Color(0xFF00696B),
        tone10 = Color(0xFF002020),
        tone20 = Color(0xFF023738),
        tone30 = Color(0xFF044F51),
        tone80 = Color(0xFF83D4D5),
        tone90 = Color(0xFFABEEEF),
    ),
    AMBER(
        seed = Color(0xFF8A5100),
        tone10 = Color(0xFF291800),
        tone20 = Color(0xFF482900),
        tone30 = Color(0xFF693C00),
        tone80 = Color(0xFFF6BB81),
        tone90 = Color(0xFFFEDCBE),
    ),
    ROSE(
        seed = Color(0xFFA0325F),
        tone10 = Color(0xFF3E001D),
        tone20 = Color(0xFF650033),
        tone30 = Color(0xFF87174A),
        tone80 = Color(0xFFFFB0C9),
        tone90 = Color(0xFFFFD9E3),
    );

    companion object {
        fun fromName(name: String?) = entries.find { it.name == name } ?: INDIGO
    }
}

/** Swaps only the primary roles; secondary, tertiary and surfaces stay on the base scheme. */
fun ColorScheme.withAccent(accent: AccentColor, darkTheme: Boolean) = copy(
    primary = if (darkTheme) accent.tone80 else accent.seed,
    onPrimary = if (darkTheme) accent.tone20 else Color.White,
    primaryContainer = if (darkTheme) accent.tone30 else accent.tone90,
    onPrimaryContainer = if (darkTheme) accent.tone90 else accent.tone10,
    inversePrimary = if (darkTheme) accent.seed else accent.tone80,
    surfaceTint = if (darkTheme) accent.tone80 else accent.seed,
    primaryFixed = accent.tone90,
    primaryFixedDim = accent.tone80,
    onPrimaryFixed = accent.tone10,
    onPrimaryFixedVariant = accent.tone30,
)
