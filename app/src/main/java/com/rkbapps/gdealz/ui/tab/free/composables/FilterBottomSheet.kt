package com.rkbapps.gdealz.ui.tab.free.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SplitButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.FreeDealsFilter
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.StoreUtil


val filterOptionsMap = mapOf(
    "Store" to R.string.filter_store,
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    appliedFilters: FreeDealsFilter = FreeDealsFilter(),
    stores: List<String> = emptyList(),
    onClearFilters: () -> Unit = {},
    onApplyFilters: (FreeDealsFilter) -> Unit
) {

    val defaultFilter = remember { FreeDealsFilter() }
    var updatedFilters by remember { mutableStateOf(appliedFilters) }
    var selectFilterOption by remember { mutableStateOf("Store") }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(37.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.filters),
                style = MaterialTheme.typography.titleLarge,
            )
            AnimatedVisibility(defaultFilter != updatedFilters) {

                SplitButton(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(
                            onClick = {
                                onApplyFilters(updatedFilters)
                            }
                        ) {
                            Text(stringResource(R.string.apply_filters))
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            onClick = onClearFilters
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.clear_filter),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth()) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .weight(1f),
            ) {
                filterOptionsMap.forEach { (key, resId) ->
                    val label = stringResource(resId)
                    if (selectFilterOption == key) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilterOption = key
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(label)
                        }
                    } else {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilterOption = key
                            }) {
                            Text(label)
                        }
                    }
                }
            }
            VerticalDivider()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .weight(1.5f),
            ) {
                when (selectFilterOption) {
                    "Store" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = stringResource(R.string.select_stores))
                            LazyColumn(
                                contentPadding = ScaffoldDefaults.contentWindowInsets.asPaddingValues(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (stores.isNotEmpty()){
                                    items(stores,) { store ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = updatedFilters.stores.contains(store),
                                                onCheckedChange = {
                                                    updatedFilters = if (it) {
                                                        updatedFilters.copy(stores = updatedFilters.stores + store)
                                                    } else {
                                                        updatedFilters.copy(stores = updatedFilters.stores - store)
                                                    }
                                                }
                                            )
                                            Text(store)
                                        }
                                    }

                                }else{
                                    item {
                                        Text(text = stringResource(R.string.no_stores_found))
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FilterBottomSheetPreview(modifier: Modifier = Modifier) {
    GDealzTheme {
        FilterBottomSheet(modifier, FreeDealsFilter()) {}
    }
}