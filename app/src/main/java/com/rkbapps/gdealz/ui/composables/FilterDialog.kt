package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.network.ShortingOptions
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.Filter


val filterOptions = listOf(
    "Store",
    "Price",
    "Sorting",
    "Order",
)


@Preview
@Composable
fun FilterDialog(
    filter: Filter = Filter(),
    stores: List<Store> = emptyList(),
    onCancel: () -> Unit = {},
    onApplyFilter: (Filter) -> Unit = {}
) {

    var updatedFilter by remember { mutableStateOf(filter) }
    var selectFilter by remember { mutableStateOf("Store") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            "Filters",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.weight(1f)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .weight(1f),
            ) {
                filterOptions.forEach {
                    if (selectFilter == it) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilter = it
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(it)
                        }
                    } else {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectFilter = it
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
                when (selectFilter) {
                    "Store" -> {
                        val store = remember { stores.find { it.storeID == "${updatedFilter.store}" } }
                        StoreFilter(
                            selectedStore = store,
                            stores = stores
                        ) { store ->
                            val storeId = try {
                                store?.storeID?.toInt()
                            } catch (_: Exception) {
                                null
                            }
                            updatedFilter = updatedFilter.copy(store = storeId)
                        }
                    }

                    "Price" -> {
                        PriceFilter(
                            upperPrice = updatedFilter.upperPrice,
                            lowerPrice = updatedFilter.lowerPrice,
                        ) { lower, upper ->
                            updatedFilter = updatedFilter.copy(
                                upperPrice = upper,
                                lowerPrice = lower
                            )
                        }
                    }

                    "Sorting" -> {
                        SortingFilter(
                            selectedSorting = updatedFilter.sortBy
                        ) {
                            updatedFilter = updatedFilter.copy(sortBy = it)
                        }
                    }

                    "Order" -> {
                        OrderFilter(
                            orderDesc = updatedFilter.orderByDesc
                        ) {
                            updatedFilter = updatedFilter.copy(orderByDesc = it)
                        }

                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = onCancel
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onApplyFilter(updatedFilter)
                }
            ) {
                Text("Apply Filter")
            }

        }
    }
}


@Composable
fun StoreFilter(
    selectedStore: Store? = null,
    stores: List<Store>,
    selectionChange: (Store?) -> Unit = {}
) {
    LazyColumn {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedStore == null,
                    onClick = {
                        selectionChange(null)
                    }
                )
                Text("All")
            }
        }
        items(stores) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedStore == it,
                    onClick = {
                        selectionChange(it)
                    }
                )
                Text(it.storeName)
            }
        }
    }

}


@Composable
fun PriceFilter(
    upperPrice: Int = 0,
    lowerPrice: Int = 0,
    onPriceUpdate: (Int, Int) -> Unit
) {
    var sliderPosition by remember {
        mutableStateOf(lowerPrice.toFloat()..upperPrice.toFloat())
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$${sliderPosition.start.toInt()}")
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$${sliderPosition.endInclusive.toInt()}")
            }
        }
        RangeSlider(
            value = sliderPosition,
            onValueChange = { range -> sliderPosition = range },
            valueRange = 0f..80f,
            onValueChangeFinished = {
                onPriceUpdate(
                    sliderPosition.start.toInt(),
                    sliderPosition.endInclusive.toInt()
                )
            }
        )
    }
}


@Composable
fun SortingFilter(
    selectedSorting: ShortingOptions,
    selectionChange: (ShortingOptions) -> Unit
) {
    LazyColumn {
        items(ShortingOptions.entries.toTypedArray()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedSorting == it,
                    onClick = {
                        selectionChange(it)
                    }
                )
                Text(it.option)
            }
        }
    }
}

@Composable
fun OrderFilter(
    orderDesc: Boolean,
    selectionChange: (Boolean) -> Unit
) {
    LazyColumn {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !orderDesc,
                    onClick = {
                        selectionChange(false)
                    }
                )
                Text("Ascending")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = orderDesc,
                    onClick = {
                        selectionChange(true)
                    }
                )
                Text("Descending")
            }
        }
    }
}



