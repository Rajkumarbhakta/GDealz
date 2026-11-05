package com.rkbapps.gdealz.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.theme.GroupedListGap
import com.rkbapps.gdealz.ui.theme.GroupedListShapes
import com.rkbapps.gdealz.util.Country
import com.rkbapps.gdealz.util.appLanguages

/** Pass a null [onDismiss] when a country is mandatory, as on first launch: there's no way out without one. */
@Composable
fun ChooseCountryDialogExpressive(
    selectedCountry: Country,
    onDismiss: (() -> Unit)?,
    onSelect: (Country) -> Unit,
) {
    PickerDialog(
        title = stringResource(R.string.choose_country),
        searchPlaceholder = stringResource(R.string.search_country),
        items = Country.entries,
        initialSelection = selectedCountry,
        itemKey = { it.key },
        itemLabel = { it.value },
        itemLeading = { it.flagEmoji() },
        matchesQuery = { country, query ->
            country.value.contains(query, ignoreCase = true) || country.key.contains(query, ignoreCase = true)
        },
        onDismiss = onDismiss,
        onConfirm = onSelect,
    )
}

@Composable
fun LanguageSelectionDialogExpressive(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    PickerDialog(
        title = stringResource(R.string.select_language),
        searchPlaceholder = stringResource(R.string.search_here),
        items = appLanguages,
        initialSelection = appLanguages.find { it.code == currentLanguageCode } ?: appLanguages.first(),
        itemKey = { it.code },
        itemLabel = { it.displayLanguage },
        itemLeading = { it.code.take(2).uppercase() },
        matchesQuery = { language, query ->
            language.displayLanguage.contains(query, ignoreCase = true) ||
                language.name.contains(query, ignoreCase = true) ||
                language.code.contains(query, ignoreCase = true)
        },
        onDismiss = onDismiss,
        onConfirm = { onLanguageSelected(it.code) },
    )
}

/** Flag emoji are the two letters of the ISO country code, as regional indicator symbols. */
private fun Country.flagEmoji(): String = key.uppercase().fold("") { flag, letter ->
    flag + String(Character.toChars(0x1F1E6 + (letter - 'A')))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T : Any> PickerDialog(
    title: String,
    searchPlaceholder: String,
    items: List<T>,
    initialSelection: T,
    itemKey: (T) -> String,
    itemLabel: (T) -> String,
    itemLeading: (T) -> String,
    matchesQuery: (T, String) -> Boolean,
    onDismiss: (() -> Unit)?,
    onConfirm: (T) -> Unit,
) {
    var selected by remember { mutableStateOf(initialSelection) }
    var query by remember { mutableStateOf("") }
    val visibleItems = remember(query) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) items else items.filter { matchesQuery(it, trimmed) }
    }
    // Opens on the current choice, which may be far down the country list.
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (items.indexOf(initialSelection) - 1).coerceAtLeast(0)
    )

    Dialog(onDismissRequest = { onDismiss?.invoke() }) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp)
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                PickerSearchField(
                    query = query,
                    placeholder = searchPlaceholder,
                    onQueryChange = { query = it },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
                )
                if (visibleItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_results_found, query.trim()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(GroupedListGap)
                    ) {
                        itemsIndexed(visibleItems, key = { _, item -> itemKey(item) }) { index, item ->
                            PickerRow(
                                label = itemLabel(item),
                                leading = itemLeading(item),
                                selected = item == selected,
                                shape = GroupedListShapes.forIndex(index, visibleItems.size),
                                onClick = { selected = item },
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onDismiss != null) {
                        TextButton(onClick = onDismiss, shapes = ButtonDefaults.shapes()) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                    Button(onClick = { onConfirm(selected) }, shapes = ButtonDefaults.shapes()) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerSearchField(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        } else null,
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PickerRow(
    label: String,
    leading: String,
    selected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer else colors.surfaceContainerHigh,
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    )
    Surface(
        selected = selected,
        onClick = onClick,
        shape = shape,
        color = container,
        contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.surfaceContainerLowest, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = leading,
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    color = colors.onSurface,
                )
            }
            Text(
                text = label,
                style = if (selected) MaterialTheme.typography.bodyLargeEmphasized
                else MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(22.dp))
            }
        }
    }
}
