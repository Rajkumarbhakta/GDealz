package com.rkbapps.gdealz.ui.tab.search

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealsItemShimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTab(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val searchResult = viewModel.searchResult.collectAsState()

    val query = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CommonTopBar(title = "Search")
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            OutlinedTextField(
                value = query.value, onValueChange = {
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
                    if (query.value.isNotEmpty() && query.value.isNotBlank()) {
                        TextButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = { viewModel.search(query.value) }) {
                            Text(text = "Search")
                        }
                    }
                },
                keyboardActions = KeyboardActions(onSearch = {
                        if (query.value.isNotEmpty()&& query.value.isNotBlank()) {
                            viewModel.search(query.value)
                        }
                    }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            when{
                searchResult.value.isLoading ->{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(count = 20, key = {
                            it.hashCode()
                        }) {
                            DealsItemShimmer()
                        }
                    }
                }
                searchResult.value.error!=null->{
                    ErrorScreen(searchResult.value.error ?: "An error occurred")
                }
                searchResult.value.data!=null->{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(10.dp))
                        }
                        items(searchResult.value.data?:emptyList(), key = {
                            it.hashCode()
                        }) { game ->
                            SearchItem(game = game) {
                                Log.d("STEAM","${game.steamAppID}")
                                if (game.steamAppID!=null){
                                    navController.navigate(
                                        Routes.SteamGameDetails(
                                            steamId = game.steamAppID,
                                            title = game.external,
                                            dealId = game.cheapestDealID
                                        )
                                    )
                                }else{
                                    navController.navigate(
                                        Routes.DealsLookup(
                                            title = game.external,
                                            dealId = game.cheapestDealID
                                        )
                                    )
                                }

                            }
                        }
                        item {
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

        }
    }

}

@Composable
fun SearchItem(game: Game, onClick: () -> Unit) {
    OutlinedCard(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier.fillMaxSize().height(90.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
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
                    model = game.thumb,
                    contentDescription = "game thumb",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                )
            }
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
            }
        }
    }


}