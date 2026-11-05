package com.rkbapps.gdealz.ui.composables

import androidx.compose.runtime.staticCompositionLocalOf
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * The backdrop the glass components refract.
 *
 * A [com.kyant.backdrop.backdrops.LayerBackdrop] only contains what has been recorded into it
 * with `Modifier.layerBackdrop(backdrop)`, so a screen that wants real glass has to record its
 * content and provide the same instance here:
 *
 * ```
 * val backdrop = rememberLayerBackdrop {
 *     drawRect(MaterialTheme.colorScheme.background)
 *     drawContent()
 * }
 * CompositionLocalProvider(LocalBackdrop provides backdrop) {
 *     Content(modifier = Modifier.layerBackdrop(backdrop)) // behind the glass
 *     CommonButton(...)                                    // drawn on top of it
 * }
 * ```
 *
 * Without that the default [emptyBackdrop] is used, there is nothing to refract, and only the
 * highlight and shadow of the glass are visible.
 */
val LocalBackdrop = staticCompositionLocalOf<Backdrop> { emptyBackdrop() }
