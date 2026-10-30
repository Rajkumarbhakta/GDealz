package com.rkbapps.gdealz.ui.tab.settings

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.BuildConfig
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.ui.theme.extendedColors
import com.rkbapps.gdealz.util.AppLocaleManager
import com.rkbapps.gdealz.util.Country

private enum class ThemeOption(val label: Int) {
    System(R.string.theme_system),
    Light(R.string.theme_light),
    Dark(R.string.theme_dark),
}

/** A settings row: either a switch, an in-app picker (chevron) or an external link. */
private sealed interface RowAction {
    data class Toggle(val checked: Boolean, val onChange: (Boolean) -> Unit) : RowAction
    data class Navigate(val onClick: () -> Unit) : RowAction
    data class Link(val onClick: () -> Unit) : RowAction
}

private data class SettingsRow(
    val key: String,
    val icon: ImageVector,
    val title: String,
    val summary: String? = null,
    val action: RowAction,
)

/** Each group gets one accent so its icons read as a set. */
private data class GroupAccent(val container: Color, val content: Color)

@Composable
fun SettingsScreenExpressive(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val isNsfw by viewModel.isNsfw.collectAsStateWithLifecycle()
    val isDynamicColor by viewModel.isDynamicTheme.collectAsStateWithLifecycle()
    val isClassicUi by viewModel.isClassicUi.collectAsStateWithLifecycle()

    val country = remember(selectedCountry) {
        Country.entries.find { it.key == selectedCountry } ?: Country.US
    }
    val languageCode = AppLocaleManager.getLanguageCode(context)

    var showCountryDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showCountryDialog) {
        Dialog(onDismissRequest = { showCountryDialog = false }) {
            ChooseCountryDialog(modifier = Modifier.height(500.dp), selectedCountry = country) {
                viewModel.updateCountry(it.key)
                showCountryDialog = false
            }
        }
    }

    if (showLanguageDialog) {
        Dialog(onDismissRequest = { showLanguageDialog = false }) {
            LanguageSelectionDialog(modifier = Modifier.height(500.dp), currentLanguageCode = languageCode) {
                viewModel.changeLanguage(it)
                showLanguageDialog = false
            }
        }
    }

    val personalize = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(
                SettingsRow(
                    key = "dynamic_color",
                    icon = Icons.Rounded.Palette,
                    title = stringResource(R.string.dynamic_color),
                    summary = stringResource(R.string.dynamic_color_desc),
                    action = RowAction.Toggle(isDynamicColor) { viewModel.updateDynamicTheme(it) }
                )
            )
        }
        add(
            SettingsRow(
                key = "classic_interface",
                icon = Icons.Rounded.History,
                title = stringResource(R.string.classic_interface),
                summary = stringResource(R.string.classic_interface_desc),
                action = RowAction.Toggle(isClassicUi) { viewModel.updateClassicUi(it) }
            )
        )
        add(
            SettingsRow(
                key = "language",
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                summary = AppLocaleManager.getLanguageFromCode(languageCode).displayLanguage,
                action = RowAction.Navigate { showLanguageDialog = true }
            )
        )
        add(
            SettingsRow(
                key = "country",
                icon = Icons.Rounded.Place,
                title = stringResource(R.string.country_and_currency),
                summary = country.value,
                action = RowAction.Navigate { showCountryDialog = true }
            )
        )
        add(
            SettingsRow(
                key = "nsfw",
                icon = Icons.Rounded.VisibilityOff,
                title = stringResource(R.string.nsfw_content),
                summary = stringResource(R.string.nsfw_content_desc),
                action = RowAction.Toggle(isNsfw) { viewModel.updateNsfwContentAllowance(it) }
            )
        )
    }

    val support = listOf(
        SettingsRow(
            key = "coffee",
            icon = Icons.Rounded.Coffee,
            title = stringResource(R.string.buy_me_a_coffee),
            summary = stringResource(R.string.support_development),
            action = RowAction.Link { uriHandler.openUri("https://coff.ee/rajkumarbhakta") }
        ),
        SettingsRow(
            key = "rate",
            icon = Icons.Rounded.Star,
            title = stringResource(R.string.rate_the_app),
            action = RowAction.Link {
                uriHandler.openUri("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            }
        ),
        SettingsRow(
            key = "issue",
            icon = Icons.Rounded.BugReport,
            title = stringResource(R.string.raise_a_issue),
            action = RowAction.Link { uriHandler.openUri("https://github.com/Rajkumarbhakta/GDealz/issues") }
        ),
        SettingsRow(
            key = "source",
            icon = Icons.Rounded.Code,
            title = stringResource(R.string.source_code),
            summary = "GitHub",
            action = RowAction.Link { uriHandler.openUri("https://github.com/Rajkumarbhakta/GDealz") }
        ),
        SettingsRow(
            key = "contact",
            icon = Icons.Rounded.Mail,
            title = stringResource(R.string.contact_developer),
            summary = "contact@rkbapps.in",
            action = RowAction.Link { uriHandler.openUri("mailto:contact@rkbapps.in") }
        ),
        SettingsRow(
            key = "privacy",
            icon = Icons.Rounded.PrivacyTip,
            title = stringResource(R.string.privacy_policy),
            action = RowAction.Link { uriHandler.openUri("https://sites.google.com/view/gdealz/home") }
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.displayMediumEmphasized,
                        modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 16.dp)
                    )
                }
            )
        }
    ) {innerPadding->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
            ,
            contentPadding = innerPadding,
        ) {

            item(key = "theme") {
                ThemeCard(
                    selected = when {
                        isSystemTheme -> ThemeOption.System
                        isDarkTheme -> ThemeOption.Dark
                        else -> ThemeOption.Light
                    },
                    onSelect = { option ->
                        viewModel.updateIsSystemTheme(option == ThemeOption.System)
                        if (option != ThemeOption.System) viewModel.updateTheme(option == ThemeOption.Dark)
                    }
                )
            }

            settingsGroup(
                title = R.string.personalize,
                rows = personalize,
                accent = {
                    GroupAccent(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            )

            settingsGroup(
                title = R.string.support_and_about,
                rows = support,
                accent = {
                    GroupAccent(
                        MaterialTheme.extendedColors.dealContainer,
                        MaterialTheme.extendedColors.onDealContainer
                    )
                }
            )

            item(key = "disclaimer") { Disclaimer(Modifier.padding(top = 18.dp)) }

            item(key = "version") {
                Text(
                    text = stringResource(
                        R.string.version_footer,
                        stringResource(R.string.app_name),
                        viewModel.appVersion
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ThemeCard(selected: ThemeOption, onSelect: (ThemeOption) -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconChip(
                    icon = Icons.Rounded.DarkMode,
                    accent = GroupAccent(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                ThemeOption.entries.forEachIndexed { index, option ->
                    ToggleButton(
                        checked = selected == option,
                        onCheckedChange = { onSelect(option) },
                        shapes = when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            ThemeOption.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        colors = ToggleButtonDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .semantics { role = Role.RadioButton }
                    ) {
                        Text(stringResource(option.label), style = MaterialTheme.typography.labelLargeEmphasized)
                    }
                }
            }
        }
    }
}

private fun LazyListScope.settingsGroup(
    title: Int,
    rows: List<SettingsRow>,
    accent: @Composable () -> GroupAccent,
) {
    item(key = "group_$title") {
        Text(
            text = stringResource(title).uppercase(),
            style = MaterialTheme.typography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, top = 18.dp, bottom = 8.dp)
        )
    }
    itemsIndexed(rows, key = { _, row -> row.key }) { index, row ->
        SettingsRowItem(
            row = row,
            shape = GroupedListShapes.forIndex(index, rows.size),
            accent = accent(),
            modifier = Modifier.padding(bottom = if (index < rows.lastIndex) 2.dp else 0.dp)
        )
    }
}

@Composable
private fun SettingsRowItem(
    row: SettingsRow,
    shape: androidx.compose.ui.graphics.Shape,
    accent: GroupAccent,
    modifier: Modifier = Modifier,
) {
    val onClick: () -> Unit = when (val action = row.action) {
        is RowAction.Toggle -> {
            { action.onChange(!action.checked) }
        }

        is RowAction.Navigate -> action.onClick
        is RowAction.Link -> action.onClick
    }
    Surface(
        onClick = onClick,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .semantics { if (row.action is RowAction.Toggle) role = Role.Switch }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconChip(row.icon, accent)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(row.title, style = MaterialTheme.typography.bodyLargeEmphasized)
                row.summary?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            when (val action = row.action) {
                is RowAction.Toggle -> Switch(
                    checked = action.checked,
                    // The whole row toggles; the switch only shows state, so screen readers hear one control.
                    onCheckedChange = null,
                    thumbContent = if (action.checked) {
                        { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) }
                    } else null,
                )

                is RowAction.Navigate -> Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                is RowAction.Link -> Icon(
                    Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun IconChip(icon: ImageVector, accent: GroupAccent) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(accent.container, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = accent.content, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Disclaimer(modifier: Modifier = Modifier) {
    Surface(
        shape = GroupedListShapes.single,
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.disclaimer_title), style = MaterialTheme.typography.titleSmallEmphasized)
                Text(
                    text = stringResource(R.string.disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
