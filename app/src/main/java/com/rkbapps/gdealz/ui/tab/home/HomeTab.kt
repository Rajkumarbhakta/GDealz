package com.rkbapps.gdealz.ui.tab.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.Navigator
import coil.compose.AsyncImage
import com.rkbapps.gdealz.models.Deals
import com.rkbapps.gdealz.ui.tab.home.viewmodel.HomeTabViewModel
import java.util.UUID


@Composable
fun HomeTab(
    navigator: Navigator
) {
    val viewModel: HomeTabViewModel = hiltViewModel()
    val deals = viewModel.deals.collectAsState()
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (deals.value.isNotEmpty()) {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(deals.value, key = {
                        it.gameID ?: "${UUID.randomUUID()}"
                    }) { deal ->
                        DealsItem(deals = deal)
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


@Composable
fun DealsItem(deals: Deals) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .height(150.dp)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = deals.thumb,
                contentDescription = "game thumb", modifier = Modifier.height(50.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = deals.title ?: "",
                    maxLines = 2,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${deals.normalPrice}",
                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$${deals.salePrice}")
                }
            }

        }


    }


}
