package com.rkbapps.gdealz.ui.screens.image_preview

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.theme.onSurfaceDark
import com.rkbapps.gdealz.ui.theme.primaryDark
import com.rkbapps.gdealz.ui.theme.surfaceContainerDark
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.abs

// The viewer is black in both themes, so its controls use the dark palette directly.
private val ControlColor = surfaceContainerDark
private val OnControlColor = onSurfaceDark
private val ThumbnailRing = primaryDark

private val DismissDistance = 120.dp

/** Hands the file to the system downloader, which needs no storage permission from API 29. */
private fun saveScreenshot(context: Context, url: String) {
    val fileName = url.substringAfterLast('/').substringBefore('?')
    val request = DownloadManager.Request(url.toUri())
        .setTitle(fileName)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "GDealz/$fileName")
    try {
        context.getSystemService(DownloadManager::class.java).enqueue(request)
        Toast.makeText(context, R.string.saving_screenshot, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, R.string.unable_to_save_screenshot, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ScreenshotViewerScreen(
    navController: NavHostController,
    imageUrls: List<String>,
    startIndex: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = startIndex) { imageUrls.size }
    val currentPage = pagerState.currentPage

    val dismissPx = with(LocalDensity.current) { DismissDistance.toPx() }
    val dragOffset = remember { Animatable(0f) }

    // Black background in both themes, so the system bar icons must stay light.
    val window = LocalActivity.current?.window
    DisposableEffect(window) {
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        val lightStatusBars = controller?.isAppearanceLightStatusBars
        val lightNavigationBars = controller?.isAppearanceLightNavigationBars
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            lightStatusBars?.let { controller.isAppearanceLightStatusBars = it }
            lightNavigationBars?.let { controller.isAppearanceLightNavigationBars = it }
        }
    }

    val goTo: (Int) -> Unit = { page ->
        scope.launch { pagerState.animateScrollToPage((page + imageUrls.size) % imageUrls.size) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // The screen fades out as it is pulled away.
                alpha = 1f - (abs(dragOffset.value) / (dismissPx * 4)).coerceIn(0f, 0.6f)
            }
            .background(Color.Black)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch { dragOffset.snapTo(dragOffset.value + delta) }
                },
                onDragStopped = {
                    if (abs(dragOffset.value) > dismissPx) {
                        navController.navigateUp()
                    } else {
                        dragOffset.animateTo(0f)
                    }
                }
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = dragOffset.value }
        ) { page ->
            val url = imageUrls[page]
            val zoomState = rememberZoomState()
            with(sharedTransitionScope) {
                AsyncImage(
                    model = url,
                    contentDescription = stringResource(R.string.screenshot_position, page + 1, imageUrls.size),
                    contentScale = ContentScale.Fit,
                    onSuccess = { zoomState.setContentSize(it.painter.intrinsicSize) },
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            rememberSharedContentState(key = url),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .zoomable(zoomState)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViewerButton(
                icon = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.close),
                onClick = { navController.navigateUp() }
            )
            Text(
                text = "${currentPage + 1} / ${imageUrls.size}",
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = OnControlColor,
                modifier = Modifier
                    .background(ControlColor, CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ViewerButton(
                    icon = Icons.Rounded.Download,
                    contentDescription = stringResource(R.string.save_screenshot),
                    onClick = { saveScreenshot(context, imageUrls[currentPage]) }
                )
            } else {
                Box(Modifier.size(48.dp))
            }
        }

        if (imageUrls.size > 1) {
            // Sits just below a 16:9 image, which is what nearly every store screenshot is.
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = maxWidth * 9 / 32 + 44.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ViewerButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.previous_screenshot),
                    width = 56.dp,
                    onClick = { goTo(currentPage - 1) }
                )
                ViewerButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.next_screenshot),
                    width = 56.dp,
                    onClick = { goTo(currentPage + 1) }
                )
            }
            ThumbnailStrip(
                imageUrls = imageUrls,
                current = currentPage,
                onSelect = goTo,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun ViewerButton(
    icon: ImageVector,
    contentDescription: String,
    width: Dp = 48.dp,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = ControlColor,
        contentColor = OnControlColor,
        modifier = Modifier.size(width = width, height = 48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription)
        }
    }
}

@Composable
private fun ThumbnailStrip(
    imageUrls: List<String>,
    current: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = current)
    LaunchedEffect(current) {
        // Keep the current thumbnail in view, with its neighbours beside it.
        listState.animateScrollToItem((current - 2).coerceAtLeast(0))
    }
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(imageUrls, key = { _, url -> url }) { index, url ->
            val selected = index == current
            val width by animateDpAsState(if (selected) 56.dp else 34.dp, MaterialTheme.motionScheme.fastSpatialSpec())
            val height by animateDpAsState(if (selected) 40.dp else 34.dp, MaterialTheme.motionScheme.fastSpatialSpec())
            val shape = RoundedCornerShape(if (selected) 14.dp else 10.dp)
            AsyncImage(
                model = url,
                contentDescription = stringResource(R.string.screenshot_position, index + 1, imageUrls.size),
                contentScale = ContentScale.Crop,
                alpha = if (selected) 1f else 0.7f,
                modifier = Modifier
                    .size(width = width, height = height)
                    .clip(shape)
                    .then(if (selected) Modifier.border(2.dp, ThumbnailRing, shape) else Modifier)
                    .clickable { onSelect(index) }
            )
        }
    }
}
