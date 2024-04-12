package com.rkbapps.gdealz.ui.tab.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.Navigator
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.ui.screens.dealslookup.DealLookupScreen
import com.rkbapps.gdealz.ui.tab.home.viewmodel.HomeTabViewModel
import com.rkbapps.gdealz.util.ShimmerBrush
import com.rkbapps.gdealz.util.calculatePercentage
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    navigator: Navigator
) {
    val viewModel: HomeTabViewModel = hiltViewModel()
    val deals = viewModel.deals.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fresh Deals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_list),
                        contentDescription = "filter deals"
                    )
                }
            }

            if (deals.value.isNotEmpty()) {
                LazyColumn() {
                    items(deals.value.size, key = {
                        deals.value[it].dealID ?: "${UUID.randomUUID()}"
                    }) { index ->
                        DealsItem(deals = deals.value[index]) {
                            navigator.push(
                                DealLookupScreen(
                                    delaId = deals.value[index].dealID,
                                    title = deals.value[index].title
                                )
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LazyColumn {
                        items(10) {
                            ShimmerDealsItem()
                        }
                    }
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsItem(deals: Deals, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
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
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "-${
                            calculatePercentage(
                                deals.normalPrice ?: "",
                                deals.salePrice ?: ""
                            )
                        }% ",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$${deals.normalPrice}",
                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if ((deals.salePrice
                                ?: "").contains("0.00")
                        ) "Free" else "$${deals.salePrice}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }


    }


}

@Composable
fun ShimmerDealsItem() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(brush = ShimmerBrush(), shape = RoundedCornerShape(8.dp))
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
                        .background(brush = ShimmerBrush(), shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(brush = ShimmerBrush(), shape = RoundedCornerShape(8.dp))
                            .height(20.dp)
                            .width(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(brush = ShimmerBrush(), shape = RoundedCornerShape(8.dp))
                            .height(20.dp)
                            .width(80.dp)

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(brush = ShimmerBrush(), shape = RoundedCornerShape(8.dp))
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

