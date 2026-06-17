package com.rkbapps.gdealz.ui.tab.fav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.CommonFilledIconButton
import com.rkbapps.gdealz.ui.composables.CommonTabs
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.DeleteAlertDialog
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.Store
import com.rkbapps.gdealz.util.StoreUtil
import kotlinx.coroutines.launch


import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.rkbapps.gdealz.ui.theme.darkGreen
import com.rkbapps.gdealz.ui.theme.normalTextColor
import com.rkbapps.gdealz.util.CurrencyAndCountryUtil
import kotlin.collections.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavTab(
    navController: NavHostController,
    viewModel: FavViewModel = hiltViewModel()
) {
    val pages = listOf(stringResource(R.string.tab_games), stringResource(R.string.tab_stores))

    val favList by viewModel.favList.collectAsStateWithLifecycle()
    val favStoreIds by viewModel.favStoreIds.collectAsStateWithLifecycle()
    val deletableFav = remember { mutableStateOf<FavDeals?>(null) }
    val isDeleteAllAlertDialogOpen = remember { mutableStateOf(false) }

    val backdrop = rememberLayerBackdrop()


    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CommonTopBar(stringResource(R.string.tab_fav), actions = {
                AnimatedVisibility(favList.isNotEmpty() && pagerState.currentPage == 0 ) {
                    Button(
                        modifier = Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedCornerShape(100.dp) },
                            effects = {
                                // vibrancy effect
                                vibrancy()
                                // blur effect
                                blur(16f.dp.toPx())
                                // lens effect
                                lens(
                                    refractionHeight = 24f.dp.toPx(),
                                    refractionAmount = 48f.dp.toPx(),
                                    // ⚠️ Use `true` for large containers,
                                    // or `false` for small containers.
                                    depthEffect = false
                                )
                            }
                        ),
                        onClick = {
                            isDeleteAllAlertDialogOpen.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(painter = painterResource(R.drawable.delete), "back")
                        Text(stringResource(R.string.delete_all))
                    }
                }
            })
        },
    ) { innerPadding ->

        if (deletableFav.value != null) {
            DeleteAlertDialog(
                warningText = stringResource(R.string.delete_single_fav_warning, deletableFav.value?.title ?: ""),
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
                warningText = stringResource(R.string.delete_all_fav_warning),
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
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                ),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, pageName ->
                    CommonTabs(
                        title = pageName,
                        isSelected = index == pagerState.currentPage
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                }
            }


            HorizontalPager(
                state = pagerState
            ) {
                when (it) {
                    0 -> {
                        FavGameListUi(
                            favList = favList,
                            navController = navController,
                        ) { deal ->
                            deletableFav.value = deal
                        }
                    }

                    1 -> {
                        FavStoreListUi(
                            favStoreIds = favStoreIds?.ids,
                            stores = StoreUtil.getStores(),
                        ) { storeId ->
                            viewModel.markStoreAsFav(storeId)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FavGameListUi(
    modifier: Modifier = Modifier,
    favList: List<FavDeals>,
    navController: NavHostController,
    onDelete: (FavDeals) -> Unit
) {
    if (favList.isEmpty()) {
        ErrorScreen(stringResource(R.string.nothing_here))
    } else {
        LazyColumn(
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Spacer(Modifier.height(10.dp))
            }
            items(favList) {
                FavItem(it, onDelete = {
                    onDelete(it)
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


@Composable
fun FavStoreListUi(
    modifier: Modifier = Modifier,
    favStoreIds: List<Int>? = null,
    stores: List<Store>? = null,
    onStoreClick: (Int) -> Unit
) {
    if (stores.isNullOrEmpty()) {
        ErrorScreen(stringResource(R.string.no_stores_found))
    } else {
        LazyColumn(
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Spacer(Modifier.height(10.dp))
            }
            items(stores, key = { it.id }) { store ->
                StoreItems(
                    store = store,
                    isFav = favStoreIds?.contains(store.id) == true,
                    onFavClick = onStoreClick
                )
            }
            item {
                Spacer(Modifier.height(10.dp))
            }
        }
    }

}


@Composable
fun StoreItems(
    modifier: Modifier = Modifier,
    store: Store,
    isFav: Boolean = false,
    onFavClick: (Int) -> Unit = {}

) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.DarkGray)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(store.image),
                        contentDescription = store.name,
                        modifier = Modifier.padding(8.dp)
                    )
                }

            Text(store.name)
            Spacer(modifier = Modifier.weight(1f))
            CommonFilledIconButton(
                icon = if (isFav) ImageVector.vectorResource(R.drawable.fav_filled)
                else ImageVector.vectorResource(R.drawable.fav_outlined),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = if (isFav) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                onFavClick(store.id)
            }
        }
    }
}

@Preview
@Composable
fun StoreItemPreview(modifier: Modifier = Modifier) {
    GDealzTheme() {
        StoreItems(
            store = StoreUtil.getStores().first()
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavItem(deals: FavDeals, onDelete: () -> Unit, onItemClick: () -> Unit) {

    val percentage = remember { deals.discountPercentage?.toInt() ?: 0 }
    val isFree = remember { (deals.discountPercentage?: 0) == 100 }

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

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = deals.title ?: "",
                    maxLines = 1,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    overflow = TextOverflow.Ellipsis,
                    //modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {

                    Text(
                        text = if (isFree) stringResource(R.string.free) else "${percentage}% OFF",
                        color = if (isFree) darkGreen else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                    Text("●")
                    Text(
                        text = "${CurrencyAndCountryUtil.currencySymbolMap[deals.currencySymbol] ?: "-"}${CurrencyAndCountryUtil.formatAmount(deals.actualPrice)}",
                        color = normalTextColor,
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    )
                    Text(
                        text = if (isFree) stringResource(R.string.free) else "${CurrencyAndCountryUtil.currencySymbolMap[deals.currencySymbol] ?: "-"}${CurrencyAndCountryUtil.formatAmount(deals.currentlyLowestPrice)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isFree) darkGreen else MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }


            FilledIconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(painter = painterResource(R.drawable.delete), "delete")
            }

        }


    }


}