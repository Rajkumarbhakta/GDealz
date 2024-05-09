package com.rkbapps.gdealz.ui.screens.dealslookup

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.api.ApiConst.IMAGE_URL
import com.rkbapps.gdealz.api.ApiConst.getFormattedDate
import com.rkbapps.gdealz.api.ApiConst.redirect
import com.rkbapps.gdealz.ui.screens.dealslookup.viewmodel.DealLookupViewModel
import com.rkbapps.gdealz.util.calculatePercentage

class DealLookupScreen(private val delaId: String?, private val title: String?) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val context = LocalContext.current
        val viewModel: DealLookupViewModel = getViewModel()
        val gameData = viewModel.gameData.collectAsState()
        val storeData = viewModel.storeData.collectAsState()
        val favStatus = viewModel.dealFavStatus.collectAsState()
        val isFav = remember {
            viewModel.isFavDeal
        }

        LaunchedEffect(key1 = Unit) {
            delaId?.let {
                Log.d("DEALSLOOKUP", " ID $it")
                viewModel.getDealsInfo(it)
                viewModel.dealFavStatus(it)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title ?: stringResource(id = R.string.app_name),
                            maxLines = 1
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator!!.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "navigation back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            delaId?.let {
                                viewModel.toggleFavDeal(gameData.value, it)
                            }
                        }
                        ) {
                            Icon(
                                imageVector = if (isFav.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "mark fav",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            )
            {
                if (gameData.value.gameInfo != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = gameData.value.gameInfo?.thumb,
                            contentDescription = "game thumb", modifier = Modifier
                                .height(80.dp)
                                .width(80.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = title ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    text = gameData.value.gameInfo?.publisher ?: "",
//                                    style = MaterialTheme.typography.titleSmall
//                                )
                            }
                        }


                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "Deal",
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "-${
                                        calculatePercentage(
                                            gameData.value.gameInfo?.retailPrice ?: "",
                                            gameData.value.gameInfo?.salePrice ?: ""
                                        )
                                    }% ",
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$${gameData.value.gameInfo?.retailPrice}",
                                    style = TextStyle(textDecoration = TextDecoration.LineThrough),

                                    )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if ((gameData.value.gameInfo?.salePrice
                                            ?: "").contains("0.00")
                                    ) "Free" else "$${gameData.value.gameInfo?.salePrice}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }


                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Ratting & Review",
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly

                            ) {
                                ReviewItems(
                                    title = "Meta Critic", value = " ${
                                        gameData.value.gameInfo?.metacriticScore ?: 0
                                    }"
                                )

                                ReviewItems(
                                    title = "Total Rating",
                                    value = getTotalReviews(
                                        count = gameData.value.gameInfo?.steamRatingCount ?: "0"
                                    )
                                )

                                ReviewItems(
                                    title = "Rate", value = "${
                                        gameData.value.gameInfo?.steamRatingPercent ?: 0
                                    }%"
                                )

                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedCard {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Overall Ratting",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = gameData.value.gameInfo?.steamRatingText
                                            ?: "Unknown",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleSmall,
                                    )

                                }


                            }

                        }

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (storeData.value != null) {
                            Card(
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Store",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedCard(modifier = Modifier.size(50.dp)) {
                                        AsyncImage(
                                            model = IMAGE_URL + storeData.value?.banner,
                                            contentDescription = "store logo",
                                            modifier = Modifier
                                                .size(50.dp)
                                                .padding(8.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = storeData.value?.storeName ?: "Unknown",
                                        textAlign = TextAlign.Center
                                    )


                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }


                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "Release Date",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = getFormattedDate(gameData.value.gameInfo?.releaseDate)
                                        ?: "Unknown",
                                    textAlign = TextAlign.Center
                                )

                            }

                        }

                    }



                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(delaId?.let { it1 ->
                                redirect(
                                    it1
                                )
                            }))
                            context.startActivity(intent)
                        }) {
                        Text(text = "Garb the deal")
                    }


                } else {

                }
            }
        }
    }

    private fun getTotalReviews(count: String): String {
        return try {
            if (count.toLong() > 1000) {
                "${count.toLong() / 1000}K"
            } else {
                count
            }
        } catch (e: Exception) {
            count
        }

    }


    @Composable
    private fun RowScope.ReviewItems(title: String, value: Any) {
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .height(50.dp)
                        .width(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }


            }
        }


    }


}

