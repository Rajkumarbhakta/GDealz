package com.rkbapps.gdealz.ui.tab.free

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.navigator.Navigator
import coil.compose.AsyncImage
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.ui.tab.free.viewmodel.FreeDealsViewModel
import com.rkbapps.gdealz.util.ErrorScreen
import com.rkbapps.gdealz.util.shimmerBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeDealsTab(navigator: Navigator) {
    val viewModel:FreeDealsViewModel = hiltViewModel()
    val giveaways = viewModel.giveaway.collectAsState()

    val currentSelectedOption = rememberSaveable {
        mutableIntStateOf(FreeGameItemsPosition.PC)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Free") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FreeOption(title = "PC", position = FreeGameItemsPosition.PC, currentSelected = currentSelectedOption){
                        viewModel.getGiveaways(currentSelectedOption.intValue)
                    }
                    FreeOption(
                        title = "Xbox",
                        position = FreeGameItemsPosition.XBOX,
                        currentSelected = currentSelectedOption
                    ){
                        viewModel.getGiveaways(currentSelectedOption.intValue)
                    }
                    FreeOption(title = "Ps4", position = FreeGameItemsPosition.PS4, currentSelected = currentSelectedOption){
                        viewModel.getGiveaways(currentSelectedOption.intValue)
                    }
                    FreeOption(
                        title = "Android",
                        position = FreeGameItemsPosition.ANDROID,
                        currentSelected = currentSelectedOption
                    ){
                        viewModel.getGiveaways(currentSelectedOption.intValue)
                    }
                    FreeOption(title = "Ios", position = FreeGameItemsPosition.IOS, currentSelected = currentSelectedOption){
                        viewModel.getGiveaways(currentSelectedOption.intValue)
                    }
                }


            }
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Games")
                Spacer(modifier = Modifier.height(10.dp))
                
                if (giveaways.value.isLoading){
                    LazyColumn {
                        items(count = 5, key = { key ->
                            key.hashCode()
                        }) {
                            FreeGameItemsShimmer()
                        }
                    }
                    
                }else{
                    if (!giveaways.value.isLoading && giveaways.value.data.isNotEmpty()){
                        LazyColumn {
                            items(count = giveaways.value.data.size, key = { key ->
                                key.hashCode()
                            }) { position ->

                                FreeGameItems(item = giveaways.value.data[position]) {

                                }


                            }
                        }
                    }else{
                        giveaways.value.userMessage?.let { 
                            ErrorScreen(it)
                        }
                    }
                }
                




                }

            }


        }


    }




@Composable
fun RowScope.FreeOption(title: String, currentSelected: MutableIntState,
                        position: Int = FreeGameItemsPosition.PC,onClick: () -> Unit) {
    Box(
        modifier =
        Modifier
            .background(
                color = if (currentSelected.intValue == position) MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .weight(1f)
            .height(35.dp)
            .clickable {
                currentSelected.intValue = position
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (currentSelected.intValue == position) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameItems(item: Giveaway, onClick: () -> Unit) {
    Card(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.image,
                contentDescription = "giveaway poster",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )


            }
        }

    }


}

@Composable
fun FreeGameItemsShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .background(brush = shimmerBrush())
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(brush = shimmerBrush(), shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}

object FreeGameItemsPosition{
    const val PC = 0
    const val XBOX = 1
    const val PS4 = 2
    const val ANDROID = 3
    const val IOS = 4
}