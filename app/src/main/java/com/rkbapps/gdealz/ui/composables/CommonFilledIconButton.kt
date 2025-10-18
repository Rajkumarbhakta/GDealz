package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun CommonFilledIconButton(
    modifier: Modifier = Modifier,
    containerColor:Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector,
    contentDescription:String? = null,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.Transparent, //Color.White.copy(alpha = 0.2f)
        contentColor = containerColor
    ),
    onClick:()-> Unit
) {

    val backdrop = rememberLayerBackdrop()


    FilledIconButton(
        modifier = modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { CircleShape },
            effects = {
                // vibrancy effect
                vibrancy()
                // blur effect
                blur(16f.dp.toPx())
                // lens effect
                lens(
                    refractionHeight = 24f.dp.toPx(),
                    refractionAmount = 48f.dp.toPx(),
                    // ⚠️ Use `true` for large containers,
                    // or `false` for small containers.
                    depthEffect = true
                )
            }
        ),
        onClick = onClick,
        colors = colors
    ) {
        Icon(imageVector = icon,contentDescription)
    }
}