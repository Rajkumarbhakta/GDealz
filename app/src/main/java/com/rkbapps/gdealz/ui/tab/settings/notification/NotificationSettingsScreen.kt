package com.rkbapps.gdealz.ui.tab.settings.notification

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.MultiSelectDialog
import com.rkbapps.gdealz.ui.tab.settings.TextWithArrow
import com.rkbapps.gdealz.ui.tab.settings.TextWithSwitch

private enum class PickerTarget { NONE, TYPES, PLATFORMS, QUIET_START, QUIET_END }

@Composable
fun NotificationSettingsScreen(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var picker by remember { mutableStateOf(PickerTarget.NONE) }

    when (picker) {
        PickerTarget.TYPES -> Dialog(onDismissRequest = { picker = PickerTarget.NONE }) {
            MultiSelectDialog(
                modifier = Modifier.height(420.dp),
                title = stringResource(R.string.content_types),
                options = viewModel.typeOptions,
                selected = prefs.types
            ) {
                viewModel.updateTypes(it)
                picker = PickerTarget.NONE
            }
        }

        PickerTarget.PLATFORMS -> Dialog(onDismissRequest = { picker = PickerTarget.NONE }) {
            MultiSelectDialog(
                modifier = Modifier.height(500.dp),
                title = stringResource(R.string.filter_store),
                options = viewModel.platformOptions,
                selected = prefs.platforms
            ) {
                viewModel.updatePlatforms(it)
                picker = PickerTarget.NONE
            }
        }

        PickerTarget.QUIET_START -> Dialog(onDismissRequest = { picker = PickerTarget.NONE }) {
            HourPickerDialog(
                title = stringResource(R.string.quiet_hours_start),
                selectedHour = prefs.quietHoursStart
            ) {
                viewModel.updateQuietHours(it, prefs.quietHoursEnd)
                picker = PickerTarget.NONE
            }
        }

        PickerTarget.QUIET_END -> Dialog(onDismissRequest = { picker = PickerTarget.NONE }) {
            HourPickerDialog(
                title = stringResource(R.string.quiet_hours_end),
                selectedHour = prefs.quietHoursEnd
            ) {
                viewModel.updateQuietHours(prefs.quietHoursStart, it)
                picker = PickerTarget.NONE
            }
        }

        PickerTarget.NONE -> Unit
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(R.string.notifications),
                isNavigationBack = true,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "giveaway toggle") {
                TextWithSwitch(
                    text = stringResource(R.string.giveaway_notifications),
                    subText = stringResource(R.string.giveaway_notifications_desc),
                    checked = prefs.giveawayEnabled,
                    icon = Icons.Default.NotificationsActive
                ) { viewModel.updateGiveawayEnabled(it) }
            }

            item(key = "types") {
                AnimatedVisibility(prefs.giveawayEnabled) {
                    ValueSettingItem(
                        icon = Icons.Default.Category,
                        text = stringResource(R.string.content_types),
                        subText = stringResource(R.string.content_types_desc),
                        value = prefs.types.summary()
                    ) { picker = PickerTarget.TYPES }
                }
            }

            item(key = "platforms") {
                AnimatedVisibility(prefs.giveawayEnabled) {
                    ValueSettingItem(
                        icon = Icons.Default.Storefront,
                        text = stringResource(R.string.filter_store),
                        subText = stringResource(R.string.stores_desc),
                        value = prefs.platforms.summary()
                    ) { picker = PickerTarget.PLATFORMS }
                }
            }

            item(key = "min worth") {
                AnimatedVisibility(prefs.giveawayEnabled) {
                    MinWorthItem(
                        minWorth = prefs.minWorth,
                        onChange = { viewModel.updateMinWorth(it) }
                    )
                }
            }

            item(key = "divider 1") { HorizontalDivider() }

            item(key = "price drop toggle") {
                TextWithSwitch(
                    text = stringResource(R.string.price_drop_notifications),
                    subText = stringResource(R.string.price_drop_notifications_desc),
                    checked = prefs.priceDropEnabled,
                    icon = Icons.Default.TrendingDown
                ) { viewModel.updatePriceDropEnabled(it) }
            }

            item(key = "divider 2") { HorizontalDivider() }

            item(key = "quiet hours") {
                TextWithSwitch(
                    text = stringResource(R.string.quiet_hours),
                    subText = stringResource(R.string.quiet_hours_desc),
                    checked = prefs.quietHoursEnabled,
                    icon = Icons.Default.Bedtime
                ) { viewModel.updateQuietHoursEnabled(it) }
            }

            item(key = "quiet hours range") {
                AnimatedVisibility(prefs.quietHoursEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HourBox(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.quiet_hours_start),
                            hour = prefs.quietHoursStart
                        ) { picker = PickerTarget.QUIET_START }
                        HourBox(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.quiet_hours_end),
                            hour = prefs.quietHoursEnd
                        ) { picker = PickerTarget.QUIET_END }
                    }
                }
            }

            item(key = "divider 3") { HorizontalDivider() }

            item(key = "test notification") {
                TextWithArrow(
                    text = stringResource(R.string.send_test_notification),
                    subText = stringResource(R.string.send_test_notification_desc),
                    icon = Icons.Default.Send
                ) { viewModel.sendTestNotification() }
            }

            item(key = "system settings") {
                TextWithArrow(
                    text = stringResource(R.string.system_notification_settings),
                    subText = stringResource(R.string.system_notification_settings_desc),
                    icon = Icons.Default.OpenInNew
                ) {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    )
                }
            }
        }
    }
}

/** An empty selection means "no restriction", so it reads as "All" rather than as nothing chosen. */
@Composable
private fun List<String>.summary(): String =
    if (isEmpty()) stringResource(R.string.all_selected) else joinToString(", ")

@Composable
private fun ValueSettingItem(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    subText: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.titleLarge)
            Text(subText, style = MaterialTheme.typography.labelSmall)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                "$value 🔽",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun MinWorthItem(
    modifier: Modifier = Modifier,
    minWorth: Int,
    onChange: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.minimum_worth),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (minWorth <= 0) stringResource(R.string.any_worth)
                else stringResource(R.string.worth_amount, minWorth),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            stringResource(R.string.minimum_worth_desc),
            style = MaterialTheme.typography.labelSmall
        )
        Slider(
            value = minWorth.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = 0f..60f,
            steps = 11
        )
    }
}

@Composable
private fun HourBox(
    modifier: Modifier = Modifier,
    label: String,
    hour: Int,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(onClick = onClick)
        ) {
            Text(hour.asHourLabel(), modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun HourPickerDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedHour: Int,
    onHourSelected: (Int) -> Unit
) {
    var current by remember(selectedHour) { mutableStateOf(selectedHour) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items((0..23).toList()) { hour ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { current = hour },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = current == hour, onClick = { current = hour })
                    Text(hour.asHourLabel())
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onHourSelected(current) }
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}

private fun Int.asHourLabel(): String = "%02d:00".format(this)
