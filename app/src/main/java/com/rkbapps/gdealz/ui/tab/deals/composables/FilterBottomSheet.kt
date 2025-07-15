package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.IsThereAnyDealSortingOptions
import com.rkbapps.gdealz.util.StoreUtil


val filterOptions = listOf(
    "Store",
    "Price",
    "Sorting",
    "Discount",
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    appliedFilters: IsThereAnyDealFilters = IsThereAnyDealFilters(),
    onApplyFilters: (IsThereAnyDealFilters) -> Unit
) {

    val defaultFilter = remember { IsThereAnyDealFilters() }

    var updatedFilters by remember { mutableStateOf(appliedFilters) }

    var selectFilterOption by remember { mutableStateOf("Store") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
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
                "Filters",
                style = MaterialTheme.typography.titleLarge,
            )
            AnimatedVisibility(defaultFilter != updatedFilters) {
                Button(
                    onClick = {
                        onApplyFilters(updatedFilters)
                    }
                ) {
                    Text("Apply Filters")
                }
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
                filterOptions.forEach {
                    if (selectFilterOption == it) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilterOption = it
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(it)
                        }
                    } else {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilterOption = it
                            }) {
                            Text(it)
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
                            Text(text = "Select Stores")
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(StoreUtil.getStores()) { store ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = updatedFilters.stores.contains(store.id),
                                            onCheckedChange = {
                                                updatedFilters = if (it) {
                                                    updatedFilters.copy(stores = updatedFilters.stores + store.id)
                                                } else {
                                                    updatedFilters.copy(stores = updatedFilters.stores - store.id)
                                                }
                                            }
                                        )
                                        Text(store.name)
                                    }
                                }
                            }

                        }
                    }

                    "Price" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            item {
                                Text(text = "Choose Price Range")
                            }
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = updatedFilters.lowerPrice == null && updatedFilters.upperPrice == null,
                                        onClick = {
                                            updatedFilters = updatedFilters.copy(
                                                upperPrice = null,
                                                lowerPrice = null,
                                            )
                                        }
                                    )
                                    Text("Any")
                                }
                            }
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = updatedFilters.lowerPrice != null || updatedFilters.upperPrice != null,
                                        onClick = {
                                            if (updatedFilters.lowerPrice == null) {
                                                updatedFilters = updatedFilters.copy(lowerPrice = 0)
                                            }
                                        }
                                    )
                                    Text("Range")
                                }
                            }
                            item {
                                if (updatedFilters.lowerPrice != null || updatedFilters.upperPrice != null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        TextField(
                                            value = updatedFilters.upperPrice?.toString() ?: "",
                                            onValueChange = {
                                                val price = it.toIntOrNull()
                                                price?.let {
                                                    updatedFilters =
                                                        updatedFilters.copy(upperPrice = price)
                                                }
                                            },
                                            placeholder = {
                                                Text("Enter upper price")
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number
                                            )
                                        )
                                        TextField(
                                            value = updatedFilters.lowerPrice?.toString() ?: "",
                                            onValueChange = {
                                                val price = it.toIntOrNull()
                                                price?.let {
                                                    updatedFilters =
                                                        updatedFilters.copy(lowerPrice = price)
                                                }

                                            },
                                            placeholder = {
                                                Text("Enter lower price")
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "Sorting" -> {
                        LazyColumn {
                            items(IsThereAnyDealSortingOptions.entries) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = it == updatedFilters.sort,
                                        onClick = {
                                            updatedFilters = updatedFilters.copy(sort = it)
                                        }
                                    )
                                    Text(it.value)
                                }
                            }
                        }
                    }

                    "Discount" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Text(text = "Choose Price Range")

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = updatedFilters.discount == null,
                                    onClick = {
                                        updatedFilters = updatedFilters.copy(discount = null)
                                    }
                                )
                                Text("Any")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = updatedFilters.discount != null,
                                    onClick = {
                                        if (updatedFilters.discount == null) {
                                            updatedFilters = updatedFilters.copy(discount = 20)
                                        }
                                    }
                                )
                                Text("Range")
                            }

                            AnimatedVisibility(updatedFilters.discount != null) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Selected : ${updatedFilters.discount ?: "20"}%")
                                    Slider(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = (updatedFilters.discount?.toFloat() ?: 20f) / 100f,
                                        onValueChange = {
                                            updatedFilters =
                                                updatedFilters.copy(discount = (it * 100).toInt())
                                        }
                                    )
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
        FilterBottomSheet(modifier, IsThereAnyDealFilters()) {}
    }
}