package com.rkbapps.gdealz.ui.composables

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

@Composable
fun CommonFilledIconButton(
    modifier: Modifier = Modifier,
    containerColor:Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector,
    contentDescription:String? = null,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.White.copy(alpha = 0.2f),
        contentColor = containerColor
    ),
    onClick:()-> Unit
) {
    FilledIconButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors
    ) {
        Icon(imageVector = icon,contentDescription)
    }
}