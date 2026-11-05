package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun CommonFilledIconButton(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector,
    contentDescription: String? = null,
    backdrop: Backdrop = LocalBackdrop.current,
    surfaceColor: Color = Color.White.copy(alpha = 0.2f),
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.Transparent,
        contentColor = containerColor
    ),
    onClick: () -> Unit
) {
    FilledIconButton(
        modifier = modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { CircleShape },
            effects = {
                vibrancy()
                blur(4f.dp.toPx())
            },
            onDrawSurface = { drawRect(surfaceColor) }
        ),
        onClick = onClick,
        colors = colors
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}
