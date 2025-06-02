package com.rkbapps.gdealz.ui.tab.search

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.ui.tab.deals.ShimmerDealsItem
import com.rkbapps.gdealz.ui.tab.search.viewmodel.SearchViewModel
import com.rkbapps.gdealz.util.ErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val searchResult = viewModel.searchResult.collectAsState()

    val query = rememberSaveable {
        mutableStateOf("")
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Search") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {

            OutlinedTextField(value = query.value, onValueChange = {
                query.value = it
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(text = "Search here...")
                },

                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "",
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                trailingIcon = {
                    if (query.value.isNotEmpty()) {
                        TextButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = { viewModel.search(query.value) }) {
                            Text(text = "Search")
                        }
                    }
                },

                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.search(query.value)
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                )
            )

            if (searchResult.value.isLoading) {
                LazyColumn {
                    items(count = 20, key = {
                        it.hashCode()
                    }) {
                        ShimmerDealsItem()
                    }
                }
            } else {
                if (!searchResult.value.isLoading && searchResult.value.data.isNotEmpty()) {
                    LazyColumn {
                        items(count = searchResult.value.data.size, key = {
                            it.hashCode()
                        }) { position ->
                            SearchItem(game = searchResult.value.data[position]) {

                            }
                        }
                    }
                } else {
                    if (searchResult.value.data.isEmpty()){
                        ErrorScreen("Nothing Found")
                    }else{
                        searchResult.value.userMessage?.let {
                            ErrorScreen(it)
                        }
                    }
                }
            }


        }
    }

}

@Composable
fun SearchItem(game: Game, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick.invoke()
        },
        elevation = CardDefaults.cardElevation(4.dp),
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
                model = game.thumb,
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
                    text = game.external ?: "",
                    maxLines = 1,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis
                )
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = "-${
//                            calculatePercentage(
//                                deals.normalPrice ?: "",
//                                deals.salePrice ?: ""
//                            )
//                        }% ",
//                        color = MaterialTheme.colorScheme.primary,
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "$${deals.normalPrice}",
//                        style = TextStyle(textDecoration = TextDecoration.LineThrough)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = if ((deals.salePrice
//                                ?: "").contains("0.00")
//                        ) "Free" else "$${deals.salePrice}"
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }


    }


}