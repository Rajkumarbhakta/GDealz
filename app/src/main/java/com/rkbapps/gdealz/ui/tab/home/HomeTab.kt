package com.rkbapps.gdealz.ui.tab.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rkbapps.gdealz.ui.screens.DealLookupScreen
import com.rkbapps.gdealz.ui.tab.home.viewmodel.HomeTabViewModel
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
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            if (deals.value.isNotEmpty()) {
                LazyColumn() {
                    items(deals.value.size, key = {
                        deals.value[it].dealID ?: "${UUID.randomUUID()}"
                    }) { index ->
                        DealsItem(deals = deals.value[index]){
                            navigator.push(DealLookupScreen(delaId = deals.value[index].dealID))
                        }
                    }
                }

            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsItem(deals: Deals,onClick:()->Unit) {
    Card(
        onClick = {
            onClick.invoke()
        },
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
                    Text(text = if ((deals.salePrice?:"").contains("0.00")) "Free" else "$${deals.salePrice}")
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }


    }


}
