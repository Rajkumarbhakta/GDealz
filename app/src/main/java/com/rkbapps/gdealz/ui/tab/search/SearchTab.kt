package com.rkbapps.gdealz.ui.tab.search

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Game
import com.rkbapps.gdealz.models.search.SearchResult
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isThereAnyDealSearchResult by viewModel.isThereAnyDealSearchResult.collectAsStateWithLifecycle()

    val query = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CommonTopBar(title = "Search")
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = searchQuery,  // query.value,
                onValueChange = {
                    viewModel.updateSearchQuery(it)
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
                    if (searchQuery.isNotEmpty() && searchQuery.isNotBlank()) {
                        TextButton(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                viewModel.search()
//                                viewModel.search(query.value)
                            }) {
                            Text(text = "Search")
                        }
                    }
                },
                keyboardActions = KeyboardActions(onSearch = {
                        if (searchQuery.isNotEmpty()&& searchQuery.isNotBlank()) {
                            viewModel.search()
                        }
                    }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            when{
                (isThereAnyDealSearchResult.data==null && searchQuery.isEmpty())->{
                    ErrorScreen("Try searching something...")
                }
                isThereAnyDealSearchResult.isLoading ->{
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
                isThereAnyDealSearchResult.error!=null->{
                    ErrorScreen(searchResult.value.error ?: "An error occurred")
                }
                isThereAnyDealSearchResult.data!=null->{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(10.dp))
                        }
                        items(isThereAnyDealSearchResult.data?:emptyList(), key = {
                            it.hashCode()
                        }) { game ->
                            SearchItem(game = game) {
                                navController.navigate(Routes.IsThereAnyDealSteamGameDetails(
                                    gameId = game.id,
                                    title = game.title
                                ))
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
                SubcomposeAsyncImage(
                    model = game.thumb,
                    contentDescription = "game thumb",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    error = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
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

@Composable
fun SearchItem(game: SearchResult, onClick: () -> Unit) {
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
                    .size(height = 78.dp, width = 50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = game.assets?.boxart,
                    contentDescription = "game thumb",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
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
                    text = game.title ?: "",
                    maxLines = 1,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }


}