package com.rkbapps.gdealz.ui.tab.deals.composables

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.theme.darkGreen
import com.rkbapps.gdealz.ui.theme.normalTextColor
import com.rkbapps.gdealz.util.calculatePercentage
import com.rkbapps.gdealz.models.deal.DealItem
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil

@Composable
fun IsThereAnyDealDealsItem(modifier: Modifier = Modifier,deal:DealItem,onClick:()->Unit) {

    val percentage = remember { deal.deal?.cut ?: 0 }
    val isFree = remember { (deal.deal?.cut ?: 0) == 100 }
    val backdrop = rememberLayerBackdrop ()

    OutlinedCard(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
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
                    .size(height = 78.dp, width = 50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = deal.assets?.boxart,
                    contentDescription = "game thumb",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        Image(
                            painter = painterResource(R.drawable.warning),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                        )
                    }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    Text(
                        text = "${CurrencyAndCountryUtil.currencySymbolMap[deal.deal?.regular?.currency] ?: "-"}${CurrencyAndCountryUtil.formatAmount(deal.deal?.regular?.amount)}",
                        color = normalTextColor,
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    )
                    Text(
                        text = if (isFree) "Free" else "${CurrencyAndCountryUtil.currencySymbolMap[deal.deal?.regular?.currency] ?: "-"}${CurrencyAndCountryUtil.formatAmount(deal.deal?.price?.amount)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isFree) darkGreen else MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }


            val textColor = if (isFree) darkGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

            Text(
                text = if (isFree) "Free" else "${percentage}%",
                color = if (isFree) darkGreen else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedCornerShape(100.dp)},
                        onDrawSurface = {
                            drawRect(textColor,
                                blendMode = BlendMode.Hue)                        },
                        effects = {
                            // vibrancy effect
                            vibrancy()
                            // blur effect
                            blur(16f.dp.toPx())
                            // lens effect
                            lens(
                                refractionHeight = 24f.dp.toPx(),
                                refractionAmount = 48f.dp.toPx(),
                                depthEffect = false
                            )
                        }
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }


    }



}