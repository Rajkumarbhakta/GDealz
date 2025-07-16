package com.rkbapps.gdealz.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.util.Country


@Preview
@Composable
fun ChooseCountryDialog(
    modifier: Modifier = Modifier,
    selectedCountry: Country = Country.US,
    onSelect:(Country)-> Unit = {}
    ) {

    var selectedCountry by remember { mutableStateOf(selectedCountry) }
    var searchQuery by remember { mutableStateOf("") }
    var countryList by remember { mutableStateOf(Country.entries.toList()) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty() && searchQuery.isNotBlank()){
           countryList  = Country.entries.filter {
                it.value.contains(searchQuery, ignoreCase = true) || it.key.contains(searchQuery)
            }
        }
    }

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
            "Choose Country",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            placeholder = {
                Text("Search country")
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(100.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(countryList){
                Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    RadioButton(
                        selected = selectedCountry == it,
                        onClick = {
                            selectedCountry = it
                        }
                    )
                    Text(it.value)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onSelect(selectedCountry)
            }
        ) {
            Text("Confirm")
        }

    }

}