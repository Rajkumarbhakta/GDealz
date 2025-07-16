package com.rkbapps.gdealz.ui.tab.fav

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.DeleteAlertDialog
import com.rkbapps.gdealz.ui.composables.ErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavTab(
    navController: NavHostController,
    viewModel: FavViewModel = hiltViewModel()
) {

    val favList by viewModel.favList.collectAsStateWithLifecycle()
    val deletableFav = remember { mutableStateOf<FavDeals?>(null) }
    val isDeleteAllAlertDialogOpen = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopBar("Fav", actions = {
                if (favList.isNotEmpty()) {
                    Button(
                        onClick = {
                            isDeleteAllAlertDialogOpen.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Delete, "back")
                        Text("Delete All")
                    }
                }
            }
            )
        }
    ) { innerPadding ->

        if (deletableFav.value != null) {
            DeleteAlertDialog(
                warningText = "Are you sure you want to delete ${deletableFav.value?.title}?",
                onDismiss = {
                    deletableFav.value = null
                },
            ) {
                deletableFav.value?.let {
                    viewModel.deleteAFav(it)
                    deletableFav.value = null
                }
            }
        }

        if (isDeleteAllAlertDialogOpen.value) {
            DeleteAlertDialog(
                warningText = "Are you sure you want to delete all favorites?",
                onDismiss = {
                    isDeleteAllAlertDialogOpen.value = false
                },
            ) {
                viewModel.deleteAllFav()
                isDeleteAllAlertDialogOpen.value = false
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (favList.isEmpty()) {
                ErrorScreen("Nothing here..")
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Spacer(Modifier.height(10.dp))
                    }
                    items(favList) {
                        FavItem(it, onDelete = {
                            deletableFav.value = it
                        }) {
                            try {
                                val idInt = it.dealID[0].digitToInt()
                                if (it.steamAppId != null) {
                                    navController.navigate(
                                        Routes.IsThereAnyDealSteamGameDetails(
                                            gameId = it.dealID,
                                            title = it.title
                                        )
                                    )
                                } else {
                                    navController.navigate(
                                        Routes.GameInfo(
                                            gameId = it.dealID,
                                            title = it.title
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                if (it.steamAppId != null) {
                                    navController.navigate(
                                        Routes.SteamGameDetails(
                                            steamId = it.steamAppId,
                                            dealId = it.dealID,
                                            title = it.title
                                        )
                                    )
                                } else {
                                    navController.navigate(
                                        Routes.DealsLookup(
                                            dealId = it.dealID,
                                            title = it.title
                                        )
                                    )
                                }
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


@Composable
fun FavItem(deals: FavDeals, onDelete: () -> Unit, onItemClick: () -> Unit) {
    OutlinedCard(
        onClick = { onItemClick.invoke() },
        modifier = Modifier
            .fillMaxSize()
            .height(90.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                    model = deals.thumb,
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

            Text(
                text = deals.title ?: "",
                maxLines = 1,
                style = TextStyle(fontWeight = FontWeight.Bold),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            FilledIconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(imageVector = Icons.Filled.Delete, "delete")
            }

        }


    }


}