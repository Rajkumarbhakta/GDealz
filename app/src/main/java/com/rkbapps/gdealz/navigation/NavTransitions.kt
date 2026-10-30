package com.rkbapps.gdealz.navigation

import androidx.activity.BackEventCompat
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute

private typealias NavScope = AnimatedContentTransitionScope<NavBackStackEntry>

private const val EnterMillis = 400
private const val ExitMillis = 250
private const val FadeThroughScale = 0.96f

// Material's emphasized decelerate curve.
private val EnterEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

private fun NavBackStackEntry.isImageViewer() =
    destination.hasRoute<Routes.ScreenshotViewer>() || destination.hasRoute<Routes.ImagePreview>()

private fun NavBackStackEntry.isDetailPage() = isImageViewer() ||
    destination.hasRoute<Routes.IsThereAnyDealSteamGameDetails>() ||
    destination.hasRoute<Routes.GameInfo>() ||
    destination.hasRoute<Routes.FreeGameDetails>()

/** The shared image carries the movement into the viewer, so the pages themselves only fade. */
private fun NavScope.involvesImageViewer() = initialState.isImageViewer() || targetState.isImageViewer()

/** Tabs are siblings, so they fade through; anything else is a step deeper or back out. */
private fun NavScope.isBetweenTabs() = !initialState.isDetailPage() && !targetState.isDetailPage()

private fun fadeThroughIn(): EnterTransition =
    fadeIn(tween(EnterMillis, easing = EnterEasing)) +
        scaleIn(tween(EnterMillis, easing = EnterEasing), initialScale = FadeThroughScale)

private fun fadeThroughOut(): ExitTransition = fadeOut(tween(ExitMillis))

private fun slideIn(fromFraction: Float): EnterTransition =
    slideInHorizontally(tween(EnterMillis, easing = EnterEasing)) { (it * fromFraction).toInt() } +
        fadeIn(tween(EnterMillis, easing = EnterEasing))

private fun slideOut(toFraction: Float): ExitTransition =
    slideOutHorizontally(tween(ExitMillis)) { (it * toFraction).toInt() } + fadeOut(tween(ExitMillis))

internal val navEnterTransition: NavScope.() -> EnterTransition = {
    when {
        involvesImageViewer() -> fadeIn(tween(ExitMillis))
        isBetweenTabs() -> fadeThroughIn()
        else -> slideIn(fromFraction = 0.25f)
    }
}

internal val navExitTransition: NavScope.() -> ExitTransition = {
    when {
        involvesImageViewer() -> fadeOut(tween(ExitMillis))
        isBetweenTabs() -> fadeThroughOut()
        else -> slideOut(toFraction = -0.1f)
    }
}

internal val navPopEnterTransition: NavScope.() -> EnterTransition = {
    when {
        involvesImageViewer() -> fadeIn(tween(ExitMillis))
        isBetweenTabs() -> fadeThroughIn()
        else -> slideIn(fromFraction = -0.1f)
    }
}

internal val navPopExitTransition: NavScope.() -> ExitTransition = {
    when {
        involvesImageViewer() -> fadeOut(tween(ExitMillis))
        isBetweenTabs() -> fadeThroughOut()
        else -> slideOut(toFraction = 0.25f)
    }
}

// Predictive back is scrubbed by the gesture, so these run linearly to track the finger 1:1.
// Both pages stay opaque while it's held, so the top page reads as a card lifting off the one
// behind it; it only fades in the last stretch, which the release plays out.
private const val PredictiveMillis = 300
private const val PredictiveFadeDelayMillis = 200

private fun predictiveDirection(swipeEdge: Int) = if (swipeEdge == BackEventCompat.EDGE_RIGHT) -1 else 1

internal val navPredictivePopEnterTransition: NavScope.(Int) -> EnterTransition = { swipeEdge ->
    val scale = scaleIn(tween(PredictiveMillis, easing = LinearEasing), initialScale = FadeThroughScale)
    if (involvesImageViewer() || isBetweenTabs()) {
        scale
    } else {
        scale + slideInHorizontally(tween(PredictiveMillis, easing = LinearEasing)) {
            -predictiveDirection(swipeEdge) * it / 8
        }
    }
}

internal val navPredictivePopExitTransition: NavScope.(Int) -> ExitTransition = { swipeEdge ->
    val scaleAndFade = scaleOut(tween(PredictiveMillis, easing = LinearEasing), targetScale = 0.9f) +
        fadeOut(tween(PredictiveMillis - PredictiveFadeDelayMillis, PredictiveFadeDelayMillis, LinearEasing))
    if (involvesImageViewer() || isBetweenTabs()) {
        scaleAndFade
    } else {
        scaleAndFade + slideOutHorizontally(tween(PredictiveMillis, easing = LinearEasing)) {
            predictiveDirection(swipeEdge) * it / 2
        }
    }
}
