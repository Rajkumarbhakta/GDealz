package com.rkbapps.gdealz.ui.screens.free_game_details.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.network.ApiConst.getFormattedDate
import com.rkbapps.gdealz.ui.composables.CommonCard

@Composable
fun FreeGameDetailsBody(modifier: Modifier = Modifier,giveaway: Giveaway?) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        SubcomposeAsyncImage(
            model = giveaway?.thumbnail,
            contentDescription = giveaway?.title,
            modifier = Modifier.fillMaxWidth(),
            error = {
                Image(
                    painter = painterResource(R.drawable.console),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            loading = {
                Image(
                    painter = painterResource(R.drawable.console),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(giveaway?.title ?: "", style = MaterialTheme.typography.titleLarge)

            Text(
                "Description",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(giveaway?.description ?: "")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CommonCard(
                    modifier = Modifier.weight(1f),
                    title = "Platform",
                    subtitle = giveaway?.platforms ?: ""
                )
                CommonCard(
                    modifier = Modifier.weight(1f),
                    title = "Type",
                    subtitle = "${giveaway?.type}"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CommonCard(
                    modifier = Modifier.weight(1f),
                    title = "Published",
                    subtitle = getFormattedDate(giveaway?.publishedDate ?: "") ?: ""
                )
                CommonCard(
                    modifier = Modifier.weight(1f),
                    title = "Offer Ending",
                    subtitle = getFormattedDate(giveaway?.endDate ?: "") ?: ""
                )
            }

            Text(
                "Instruction",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(giveaway?.instructions ?: "")

        }
    }
}