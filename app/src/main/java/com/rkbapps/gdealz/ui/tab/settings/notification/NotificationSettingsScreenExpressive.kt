package com.rkbapps.gdealz.ui.tab.settings.notification

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings as SettingsIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.tab.free.composables.SelectableChip
import com.rkbapps.gdealz.ui.tab.settings.GroupAccent
import com.rkbapps.gdealz.ui.tab.settings.GroupHeader
import com.rkbapps.gdealz.ui.tab.settings.RowAction
import com.rkbapps.gdealz.ui.tab.settings.SettingsRow
import com.rkbapps.gdealz.ui.tab.settings.SettingsRowItem
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import kotlin.math.roundToInt

private enum class QuietHourTarget { Start, End }

private val RowGap = 2.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationSettingsScreenExpressive(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var quietHourTarget by remember { mutableStateOf<QuietHourTarget?>(null) }

    val giftAccent = GroupAccent(MaterialTheme.extendedColors.giftContainer, MaterialTheme.extendedColors.onGiftContainer)
    val dealAccent = GroupAccent(MaterialTheme.extendedColors.dealContainer, MaterialTheme.extendedColors.onDealContainer)
    val deliveryAccent = GroupAccent(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)

    val giveawayRows = if (prefs.giveawayEnabled) 4 else 1
    val deliveryRows = if (prefs.quietHoursEnabled) 4 else 3

    quietHourTarget?.let { target ->
        HourPickerDialog(
            title = stringResource(if (target == QuietHourTarget.Start) R.string.quiet_hours_start else R.string.quiet_hours_end),
            selectedHour = if (target == QuietHourTarget.Start) prefs.quietHoursStart else prefs.quietHoursEnd,
            onDismiss = { quietHourTarget = null },
            onConfirm = { hour ->
                if (target == QuietHourTarget.Start) {
                    viewModel.updateQuietHours(hour, prefs.quietHoursEnd)
                } else {
                    viewModel.updateQuietHours(prefs.quietHoursStart, hour)
                }
                quietHourTarget = null
            }
        )
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.notifications)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
        ) {
            item(key = "giveaways_header") { GroupHeader(R.string.channel_group_giveaways) }
            item(key = "new_free_games") {
                SettingsRowItem(
                    row = SettingsRow(
                        key = "new_free_games",
                        icon = Icons.Rounded.CardGiftcard,
                        title = stringResource(R.string.new_free_games),
                        summary = stringResource(R.string.giveaway_notifications_desc),
                        action = RowAction.Toggle(prefs.giveawayEnabled) { viewModel.updateGiveawayEnabled(it) }
                    ),
                    shape = GroupedListShapes.forIndex(0, giveawayRows),
                    accent = giftAccent,
                    modifier = Modifier.animateItem()
                )
            }
            if (prefs.giveawayEnabled) {
                item(key = "content_types") {
                    MultiSelectCard(
                        title = stringResource(R.string.content_types),
                        summary = stringResource(R.string.content_types_desc),
                        options = viewModel.typeOptions,
                        selected = prefs.types,
                        onChange = { viewModel.updateTypes(it) },
                        shape = GroupedListShapes.forIndex(1, giveawayRows),
                        modifier = Modifier
                            .animateItem()
                            .padding(top = RowGap)
                    )
                }
                item(key = "stores") {
                    MultiSelectCard(
                        title = stringResource(R.string.stores_label),
                        summary = stringResource(R.string.stores_desc),
                        options = viewModel.platformOptions,
                        selected = prefs.platforms,
                        onChange = { viewModel.updatePlatforms(it) },
                        shape = GroupedListShapes.forIndex(2, giveawayRows),
                        modifier = Modifier
                            .animateItem()
                            .padding(top = RowGap)
                    )
                }
                item(key = "min_worth") {
                    MinWorthCard(
                        minWorth = prefs.minWorth,
                        onChange = { viewModel.updateMinWorth(it) },
                        shape = GroupedListShapes.forIndex(3, giveawayRows),
                        modifier = Modifier
                            .animateItem()
                            .padding(top = RowGap)
                    )
                }
            }

            item(key = "favorites_header") {
                GroupHeader(R.string.favorites_title, Modifier.animateItem())
            }
            item(key = "price_drops") {
                SettingsRowItem(
                    row = SettingsRow(
                        key = "price_drops",
                        icon = Icons.Rounded.Favorite,
                        title = stringResource(R.string.price_drops_on_favorites),
                        summary = stringResource(R.string.price_drop_notifications_desc),
                        action = RowAction.Toggle(prefs.priceDropEnabled) { viewModel.updatePriceDropEnabled(it) }
                    ),
                    shape = GroupedListShapes.single,
                    accent = dealAccent,
                    modifier = Modifier.animateItem()
                )
            }

            item(key = "delivery_header") {
                GroupHeader(R.string.notification_delivery, Modifier.animateItem())
            }
            item(key = "quiet_hours") {
                SettingsRowItem(
                    row = SettingsRow(
                        key = "quiet_hours",
                        icon = Icons.Rounded.Schedule,
                        title = stringResource(R.string.quiet_hours),
                        summary = stringResource(R.string.quiet_hours_desc),
                        action = RowAction.Toggle(prefs.quietHoursEnabled) { viewModel.updateQuietHoursEnabled(it) }
                    ),
                    shape = GroupedListShapes.forIndex(0, deliveryRows),
                    accent = deliveryAccent,
                    modifier = Modifier.animateItem()
                )
            }
            if (prefs.quietHoursEnabled) {
                item(key = "quiet_hours_range") {
                    QuietHoursCard(
                        startHour = prefs.quietHoursStart,
                        endHour = prefs.quietHoursEnd,
                        onPick = { quietHourTarget = it },
                        shape = GroupedListShapes.forIndex(1, deliveryRows),
                        modifier = Modifier
                            .animateItem()
                            .padding(top = RowGap)
                    )
                }
            }
            item(key = "test_notification") {
                SettingsRowItem(
                    row = SettingsRow(
                        key = "test_notification",
                        icon = Icons.AutoMirrored.Rounded.Send,
                        title = stringResource(R.string.send_test_notification),
                        summary = stringResource(R.string.send_test_notification_desc),
                        action = RowAction.Navigate { viewModel.sendTestNotification() }
                    ),
                    shape = GroupedListShapes.forIndex(deliveryRows - 2, deliveryRows),
                    accent = deliveryAccent,
                    modifier = Modifier
                        .animateItem()
                        .padding(top = RowGap)
                )
            }
            item(key = "system_settings") {
                SettingsRowItem(
                    row = SettingsRow(
                        key = "system_settings",
                        icon = Icons.Rounded.SettingsIcon,
                        title = stringResource(R.string.system_notification_settings),
                        summary = stringResource(R.string.system_notification_settings_desc),
                        action = RowAction.Link {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            )
                        }
                    ),
                    shape = GroupedListShapes.forIndex(deliveryRows - 1, deliveryRows),
                    accent = deliveryAccent,
                    modifier = Modifier
                        .animateItem()
                        .padding(top = RowGap, bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            content = content
        )
    }
}

@Composable
private fun CardHeading(title: String, summary: String) {
    Text(title, style = MaterialTheme.typography.bodyLargeEmphasized)
    Text(
        text = summary,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 2.dp)
    )
}

/** An empty selection means "no restriction", so "All" is the chip that clears it. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiSelectCard(
    title: String,
    summary: String,
    options: List<String>,
    selected: List<String>,
    onChange: (List<String>) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    SettingsCard(shape = shape, modifier = modifier) {
        CardHeading(title, summary)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 6.dp)
        ) {
            SelectableChip(
                label = stringResource(R.string.all_selected),
                selected = selected.isEmpty(),
                onClick = { onChange(emptyList()) }
            )
            options.forEach { option ->
                SelectableChip(
                    label = option,
                    selected = option in selected,
                    onClick = { onChange(if (option in selected) selected - option else selected + option) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MinWorthCard(
    minWorth: Int,
    onChange: (Int) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    // Follows the finger locally and saves once on release, so a drag doesn't write every step.
    val sliderState = remember(minWorth) {
        SliderState(value = minWorth.toFloat(), steps = 11, trackRange = 0f..60f)
    }
    val amount = sliderState.value.roundToInt()

    SettingsCard(shape = shape, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                CardHeading(stringResource(R.string.minimum_worth), stringResource(R.string.minimum_worth_desc))
            }
            Text(
                text = if (amount <= 0) stringResource(R.string.any_worth) else stringResource(R.string.worth_amount, amount),
                style = PriceTextStyles.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Slider(
            state = sliderState,
            onValueChange = { sliderState.value = it },
            onValueChangeFinished = { onChange(sliderState.value.roundToInt()) },
            track = {
                SliderDefaults.Track(
                    sliderState = it,
                    trackCornerSize = 8.dp,
                    modifier = Modifier.height(16.dp),
                )
            },
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun QuietHoursCard(
    startHour: Int,
    endHour: Int,
    onPick: (QuietHourTarget) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    SettingsCard(shape = shape, modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HourTile(
                label = stringResource(R.string.quiet_hours_start),
                hour = startHour,
                onClick = { onPick(QuietHourTarget.Start) },
                modifier = Modifier.weight(1f)
            )
            HourTile(
                label = stringResource(R.string.quiet_hours_end),
                hour = endHour,
                onClick = { onPick(QuietHourTarget.End) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HourTile(label: String, hour: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(hour.asHourLabel(), style = MaterialTheme.typography.headlineSmallEmphasized)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HourPickerDialog(
    title: String,
    selectedHour: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var current by remember(selectedHour) { mutableIntStateOf(selectedHour) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                (0..23).chunked(4).forEach { hours ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        hours.forEach { hour ->
                            ToggleButton(
                                checked = current == hour,
                                onCheckedChange = { current = hour },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = hour.asHourLabel(),
                                    style = MaterialTheme.typography.labelLargeEmphasized,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Button(onClick = { onConfirm(current) }, shapes = ButtonDefaults.shapes()) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}
