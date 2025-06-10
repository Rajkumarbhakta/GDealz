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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rkbapps.gdealz.util.shimmerBrush

@Composable
fun DealsItemShimmer() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                    .height(80.dp)
                    .width(80.dp)
                    .align(alignment = Alignment.CenterVertically)


            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                            .height(20.dp)
                            .width(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                            .height(20.dp)
                            .width(80.dp)

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                            .height(20.dp)
                            .width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }


    }
}