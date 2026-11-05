package com.rkbapps.gdealz.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.theme.AccentColor

private val AccentColor.label: Int
    get() = when (this) {
        AccentColor.INDIGO -> R.string.accent_indigo
        AccentColor.TEAL -> R.string.accent_teal
        AccentColor.AMBER -> R.string.accent_amber
        AccentColor.ROSE -> R.string.accent_rose
    }

/** A row of round swatches, one per [AccentColor]; the chosen one shows a check inside a ring. */
@Composable
fun AccentColorPicker(
    selected: AccentColor,
    onSelect: (AccentColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AccentColor.entries.forEach { accent ->
            val isSelected = accent == selected
            val label = stringResource(accent.label)
            // The gap between ring and swatch opens up on selection.
            val inset by animateDpAsState(if (isSelected) 6.dp else 0.dp, label = "accentInset")
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) accent.seed else Color.Transparent,
                        shape = CircleShape
                    )
                    .selectable(selected = isSelected, role = Role.RadioButton) { onSelect(accent) }
                    .semantics { contentDescription = label }
                    .padding(inset)
                    .background(accent.seed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Explicit receiver: inside Row's scope the RowScope overload would otherwise be picked.
                androidx.compose.animation.AnimatedVisibility(visible = isSelected, enter = scaleIn(), exit = scaleOut()) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/** Dynamic color overrides any accent, so the picker only makes sense when it's off or unsupported. */
fun isAccentPickerVisible(isDynamicColor: Boolean): Boolean =
    !isDynamicColor || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S
