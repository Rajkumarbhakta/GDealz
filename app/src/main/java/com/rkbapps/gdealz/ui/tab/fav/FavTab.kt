package com.rkbapps.gdealz.ui.tab.fav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.util.ErrorScreen

@Composable
fun FavTab(
    navController: NavHostController,
    viewModel: FavViewModel = hiltViewModel()
) {

    val favList = viewModel.favList.collectAsStateWithLifecycle()
    val deletableFav = remember { mutableStateOf<FavDeals?>(null) }

    Scaffold(
        topBar = {
            CommonTopBar("Fav")
        }
    ) {

        if (deletableFav.value != null) {
            AlertDialog(
                onDismissRequest = {
                    deletableFav.value = null
                },
                title = {
                    Text("Delete?")
                },
                text = {
                    Text("Are you sure you want to delete ${deletableFav.value?.title} ?")
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            deletableFav.value?.let {
                                viewModel.deleteAFav(it)
                                deletableFav.value = null
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            deletableFav.value = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            if (favList.value.isEmpty()) {
                ErrorScreen("Nothing here..")
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(favList.value) {
                        FavItem(it, onDelete = {
                            deletableFav.value = it
                        }) {
                            navController.navigate(
                                Routes.DealsLookup(
                                    title = it.title,
                                    dealId = it.dealID
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FavItem(deals: FavDeals, onDelete: () -> Unit, onItemClick: () -> Unit) {
    Card(
        onClick = {
            onItemClick.invoke()
        },
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
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

            Text(
                text = deals.title ?: "",
                maxLines = 1,
                style = TextStyle(fontWeight = FontWeight.Bold),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, "delete")
            }

        }


    }


}