package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = LocalBackdrop.current,
    surfaceColor: Color = Color.White.copy(alpha = 0.2f),
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        modifier = modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(100.dp) },
            effects = {
                vibrancy()
                blur(4f.dp.toPx())
            },
            // glass is never fully clear - a thin surface tint is what makes it read as glass
            onDrawSurface = { drawRect(surfaceColor) }
        ),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        content = content
    )
}
