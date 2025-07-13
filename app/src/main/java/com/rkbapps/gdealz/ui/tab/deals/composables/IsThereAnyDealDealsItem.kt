package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rkbapps.gdealz.ui.theme.darkGreen
import com.rkbapps.gdealz.ui.theme.normalTextColor
import com.rkbapps.gdealz.util.calculatePercentage
import com.rkbapps.gdealz.models.deal.DealItem

@Composable
fun IsThereAnyDealDealsItem(modifier: Modifier = Modifier,deal:DealItem,onClick:()->Unit) {

    val percentage = remember { deal.deal?.cut ?: 0 }
    val isFree = remember { (deal.deal?.cut ?: 0) == 100 }

    OutlinedCard(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = deal.assets?.boxart,
                    contentDescription = "game thumb",
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = deal.title ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis
                )
                //Text("⭐ ${deals.dealRating ?: 0.0}", color = normalTextColor)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    Text(
                        text = "$${deal.deal?.regular?.amount?:0}",
                        color = normalTextColor,
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    )
                    Text(
                        text = if (isFree) "Free" else "$${deal.deal?.price?.amount?:0}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isFree) darkGreen else MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }


            Text(
                text = if (isFree) "Free" else "${percentage}%",
                color = if (isFree) darkGreen else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        color =
                            if (isFree) darkGreen.copy(alpha = 0.2f) else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ).padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }


    }



}