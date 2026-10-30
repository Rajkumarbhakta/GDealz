package com.rkbapps.gdealz.ui.tab.free.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.FreeDealsFilter
import com.rkbapps.gdealz.ui.theme.GDealzTheme

/** A connected group only reads well with a few short options; more types fall back to chips. */
private const val MaxTypesInGroup = 3

/** Stores shown before "Show all" (selected ones are always shown). */
private const val CollapsedStoreCount = 10

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetExpressive(
    modifier: Modifier = Modifier,
    appliedFilters: FreeDealsFilter = FreeDealsFilter(),
    stores: List<String> = emptyList(),
    types: List<String> = emptyList(),
    /** Number of giveaways a filter would show; filtering is local, so the button can say it. */
    resultCount: ((FreeDealsFilter) -> Int)? = null,
    onCancel: () -> Unit = {},
    onApplyFilters: (FreeDealsFilter) -> Unit,
) {
    val defaultFilter = remember { FreeDealsFilter() }
    var draft by remember { mutableStateOf(appliedFilters) }
    var showAllStores by remember { mutableStateOf(false) }

    val visibleStores = if (showAllStores || stores.size <= CollapsedStoreCount) stores else {
        val selected = stores.filter { it in draft.stores }
        selected + (stores - selected.toSet()).take(CollapsedStoreCount)
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 6.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.headlineMediumEmphasized
            )
            TextButton(enabled = draft != defaultFilter, onClick = { draft = defaultFilter }) {
                Text(stringResource(R.string.reset_all), style = MaterialTheme.typography.labelLargeEmphasized)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel(stringResource(R.string.type))
            if (types.isEmpty()) {
                EmptyHint(stringResource(R.string.nothing_here))
            } else if (types.size <= MaxTypesInGroup) {
                // "All" plus each type; types are multi-select, "All" clears them.
                val options: List<String?> = listOf(null) + types
                Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                    options.forEachIndexed { index, type ->
                        val checked = if (type == null) draft.types.isEmpty() else type in draft.types
                        ToggleButton(
                            checked = checked,
                            onCheckedChange = {
                                draft = draft.copy(
                                    types = when {
                                        type == null -> emptyList()
                                        checked -> draft.types - type
                                        else -> draft.types + type
                                    }
                                )
                            },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
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
                                .height(48.dp)
                        ) {
                            Text(
                                text = type ?: stringResource(R.string.all),
                                style = MaterialTheme.typography.labelLargeEmphasized,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { type ->
                        SelectableChip(
                            label = type,
                            selected = type in draft.types,
                            onClick = {
                                draft = draft.copy(
                                    types = if (type in draft.types) draft.types - type else draft.types + type
                                )
                            }
                        )
                    }
                }
            }

            SectionLabel(stringResource(R.string.stores_label), Modifier.padding(top = 22.dp, bottom = 10.dp))
            if (stores.isEmpty()) {
                EmptyHint(stringResource(R.string.no_stores_found))
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.animateContentSize(MaterialTheme.motionScheme.defaultSpatialSpec())
                ) {
                    visibleStores.forEach { store ->
                        SelectableChip(
                            label = store,
                            selected = store in draft.stores,
                            onClick = {
                                draft = draft.copy(
                                    stores = if (store in draft.stores) draft.stores - store else draft.stores + store
                                )
                            }
                        )
                    }
                }
                if (stores.size > CollapsedStoreCount) {
                    TextButton(onClick = { showAllStores = !showAllStores }) {
                        Text(
                            if (showAllStores) stringResource(R.string.show_fewer_stores)
                            else stringResource(R.string.show_all_stores, stores.size)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight)
            ) {
                Text(stringResource(R.string.cancel), style = MaterialTheme.typography.titleMediumEmphasized)
            }
            val count = resultCount?.invoke(draft)
            Button(
                onClick = { onApplyFilters(draft) },
                // Applying a filter that matches nothing would just show an empty list.
                enabled = count != 0,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .weight(1f)
                    .height(ButtonDefaults.MediumContainerHeight)
            ) {
                Text(
                    text = if (count != null) pluralStringResource(R.plurals.show_n_giveaways, count, count)
                    else stringResource(R.string.apply_filters),
                    style = MaterialTheme.typography.titleMediumEmphasized
                )
            }
        }
    }
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null,
        shape = MaterialTheme.shapes.medium,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier.padding(bottom = 10.dp)) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmallEmphasized,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 4.dp)
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun FilterBottomSheetExpressivePreview() {
    GDealzTheme(dynamicColor = false) {
        FilterBottomSheetExpressive(
            appliedFilters = FreeDealsFilter(stores = listOf("Steam"), types = listOf("DLC")),
            stores = listOf("Epic Games Store", "GOG", "Itch.io", "PC", "Steam", "Ubisoft"),
            types = listOf("DLC", "Early Access", "Game"),
            resultCount = { 4 },
        ) {}
    }
}
