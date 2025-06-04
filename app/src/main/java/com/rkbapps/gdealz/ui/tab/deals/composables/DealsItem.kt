package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.util.calculatePercentage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsItem(deals: Deals, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            AsyncImage(
                model = deals.thumb,
                contentDescription = "game thumb",
                modifier = Modifier
                    .weight(0.2f)
                    .align(alignment = Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f)
            ) {
                Text(
                    text = deals.title ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val percentage = remember { calculatePercentage(deals.normalPrice ?: "", deals.salePrice ?: "") }
                    Text(
                        text = "-${percentage}% ",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "$${deals.normalPrice}",
                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                    Text(
                        text = if ((deals.salePrice ?: "").contains("0.00")) "Free" else "$${deals.salePrice}"
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }


    }


}