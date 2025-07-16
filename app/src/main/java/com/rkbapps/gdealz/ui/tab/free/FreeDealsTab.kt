package com.rkbapps.gdealz.ui.tab.free

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.util.getStatusFromEndDate
import com.rkbapps.gdealz.util.shimmerBrush
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private val options = listOf(
    "Un Claimed",
    "Claimed"
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeDealsTab(
    navController: NavHostController,
    viewModel: FreeDealsViewModel = hiltViewModel()
) {

    val giveaways = viewModel.giveaways.collectAsStateWithLifecycle()

    val unClaimedGiveaway = viewModel.unClaimedGiveaway.collectAsStateWithLifecycle()
    val claimedGiveaway = viewModel.claimedGiveaway.collectAsStateWithLifecycle()

    val giveawayState = viewModel.giveawayState.collectAsStateWithLifecycle()

    val currentSelectedOption = rememberSaveable { mutableIntStateOf(FreeGameItemsPosition.PC) }

    val selectedTab = rememberSaveable { mutableStateOf(options.first()) }

    Scaffold(
        topBar = { CommonTopBar(title = "Free") },
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {

            if (!giveawayState.value.isLoading && giveawayState.value.error==null){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    options.forEach {
                        Tabs(it,selectedTab.value==it) {
                            selectedTab.value = it
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Games")
                Spacer(modifier = Modifier.height(10.dp))
                when {
                    giveawayState.value.isLoading -> {
                        LazyColumn {
                            items(count = 5, key = { key ->
                                key.hashCode()
                            }) {
                                FreeGameItemsShimmer()
                            }
                        }
                    }

                    giveawayState.value.error != null -> {
                        ErrorScreen(giveawayState.value.error ?: "Something went wrong")
                    }

                    giveaways.value.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {

                            if (selectedTab.value == options.first() && unClaimedGiveaway.value.isEmpty()) {
                                item { ErrorScreen("No active giveaways available at the moment, please try again later.") }
                            }
                            if (selectedTab.value != options.first() && claimedGiveaway.value.isEmpty()) {
                                item { ErrorScreen("You have not claimed anything yet.") }
                            }

                            items(
                                if (selectedTab.value == options.first()) unClaimedGiveaway.value else claimedGiveaway.value,
                                key = { it.id }
                            ) {
                                FreeGameItems(it) {
                                    val giveaway = viewModel.getGiveawayJson(it)
                                    navController.navigate(Routes.FreeGameDetails(giveaway))
                                }
                            }
                        }
                    }

                    else -> {
                        ErrorScreen("No active giveaways available at the moment, please try again later.")
                    }
                }
            }
        }


    }


}


@Composable
fun RowScope.FreeOption(
    title: String, currentSelected: MutableIntState,
    position: Int = FreeGameItemsPosition.PC,
    onClick: () -> Unit
) {
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

@Composable
fun RowScope.Tabs(
    title: String,
    isSelected: Boolean,
    onTabSelect: () -> Unit
) {
    Box(
        modifier =
            Modifier
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .weight(1f)
                .height(35.dp)
                .clickable(onClick = onTabSelect),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
        )
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeGameItems(item: Giveaway, onClick: () -> Unit) {

    val status = remember {
        getStatusFromEndDate(item.endDate)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            Log.d("STATUS ","$status")
        }
    }

    Card(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Box {
            Column(Modifier.fillMaxWidth()) {
                SubcomposeAsyncImage(
                    model = item.image,
                    contentDescription = "giveaway poster",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    error = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    loading = {
                        Image(
                            painter = painterResource(R.drawable.console),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
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
            status?.let {
                Box(
                    Modifier
                        .background(
                            color = if (it) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 15.dp)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (it) "Active" else "Expired",
                        color = if (it) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error
                    )
                }
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

object FreeGameItemsPosition {
    const val PC = 0
    const val XBOX = 1
    const val PS4 = 2
    const val ANDROID = 3
    const val IOS = 4
}