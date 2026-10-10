package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.R

/**
 * Picks any number of values from [options].
 *
 * An empty result is meaningful: callers treat it as "no restriction" rather than "nothing chosen",
 * which is why "Clear" reads as selecting everything.
 */
@Composable
fun MultiSelectDialog(
    modifier: Modifier = Modifier,
    title: String,
    options: List<String>,
    selected: List<String>,
    onConfirm: (List<String>) -> Unit
) {
    var current by remember(selected) { mutableStateOf(selected) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { current = options }) {
                Text(stringResource(R.string.select_all))
            }
            TextButton(onClick = { current = emptyList() }) {
                Text(stringResource(R.string.clear_selection))
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(options) { option ->
                val checked = option in current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            current = if (checked) current - option else current + option
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            current = if (it) current + option else current - option
                        }
                    )
                    Text(option)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onConfirm(current) }
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}
