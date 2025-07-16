package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    title: String,
    isNavigationBack: Boolean = false,
    actions: @Composable (RowScope.() -> Unit) = {},
    onNavigateBack: () -> Unit = {}
){
    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.secondary)
            )
        ),
        navigationIcon = {
            if (isNavigationBack){
                CommonFilledIconButton (
                    onClick = onNavigateBack,
                    icon = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        actions = actions
    )
}