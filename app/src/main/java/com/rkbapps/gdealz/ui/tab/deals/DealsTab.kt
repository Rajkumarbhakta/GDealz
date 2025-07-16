package com.rkbapps.gdealz.ui.tab.deals

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.composables.CommonFilledIconButton
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealsItemShimmer
import com.rkbapps.gdealz.ui.tab.deals.composables.FilterBottomSheet
import com.rkbapps.gdealz.ui.tab.deals.composables.IsThereAnyDealDealsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Route


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsTab(navController: NavHostController, viewModel: DealsTabViewModel = hiltViewModel()) {

    //val dealsPagingData = viewModel.dealsPagingData.collectAsLazyPagingItems()
    //val filter = viewModel.filter.collectAsStateWithLifecycle()
    //val stores = viewModel.stores.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val minHeight = configuration.screenHeightDp * 0.5f // 50% of screen height
    val maxHeight = configuration.screenHeightDp

    val filter by viewModel.isThereAnyDealFilter.collectAsStateWithLifecycle()
    val country by viewModel.country.collectAsStateWithLifecycle()

    val isThereAnyDealPager = viewModel.isThereAnyDeals.collectAsLazyPagingItems()

    val isFilterDialogVisible = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }

    val isChooseCountryDialogOpen = remember { mutableStateOf(false) }


    val defaultFilter = remember { IsThereAnyDealFilters() }

    LaunchedEffect(country) {
        delay(200)
        if (country==null){
            isChooseCountryDialogOpen.value = true
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(title = stringResource(R.string.app_name),
                actions = {
                if (filter != defaultFilter) {
                    Button(
                        onClick = { viewModel.clearIsThereAnyDealFilter() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Clear Filter")
                    }
                }
                    CommonFilledIconButton(
                        icon = Icons.Default.Settings
                    ) {
                        navController.navigate(Routes.Settings)
                    }
            })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        /*if (isFilterDialogVisible.value) {
            Dialog(onDismissRequest = {
                isFilterDialogVisible.value = !isFilterDialogVisible.value
            }) {
                FilterDialog(
                    filter = filter.value,
                    stores = stores.value,
                    onCancel = { isFilterDialogVisible.value = false }
                ) {
                    viewModel.updateFilter(it)
                    isFilterDialogVisible.value = false
                }
            }
        }*/

        if (isChooseCountryDialogOpen.value){
            Dialog(onDismissRequest = {}) {
                ChooseCountryDialog(modifier = Modifier.height(500.dp)) {
                    viewModel.updateCountry(it.key)
                    isChooseCountryDialogOpen.value = false
                }
            }
        }

        if (showBottomSheet.value) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxWidth().heightIn(min = minHeight.dp, max = maxHeight.dp),
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState
            ) {
                FilterBottomSheet(
                    appliedFilters = filter
                ) {
                    viewModel.updateIsThereAnyDealFilter(it)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet.value = false
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fresh Deals",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.W400,
                    )
                    Text("Hot gaming deals updated daily")
                }
                IconButton(onClick = {
                    isFilterDialogVisible.value = true
                    showBottomSheet.value = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_list),
                        contentDescription = "filter deals"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                when (isThereAnyDealPager.loadState.refresh) {
                    is LoadState.Loading -> {
                        items(10) {
                            DealsItemShimmer()
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            val error = remember { isThereAnyDealPager.loadState.refresh as LoadState.Error }
                            ErrorScreen(error.error.message?:"Something went wrong.")
                        }
                    }

                    is LoadState.NotLoading -> {}
                }

                if (isThereAnyDealPager.itemCount <= 0) {
                    item {
                        ErrorScreen("No Deals Found!")
                    }
                }

                items(count = isThereAnyDealPager.itemCount) { position ->
                    isThereAnyDealPager[position]?.let { deal ->
                        IsThereAnyDealDealsItem(deal = deal) {
                            deal.deal?.let {
                                navController.navigate(
                                    Routes.IsThereAnyDealSteamGameDetails(
                                        gameId = deal.id,
                                        title = deal.title,
                                    )
                                )
                            }
                        }
                    }
                }
                when (isThereAnyDealPager.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp), contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Text(
                                "Something went wrong!",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                            )
                        }
                    }

                    is LoadState.NotLoading -> {}
                }

            }

            /*if (false) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    when (dealsPagingData.loadState.refresh) {
                        is LoadState.Loading -> {
                            items(10) {
                                DealsItemShimmer()
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                ErrorScreen("Something went wrong...")
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }

                    if (dealsPagingData.itemCount <= 0) {
                        item {
                            ErrorScreen("No Deals Found!")
                        }
                    }

                    items(
                        count = dealsPagingData.itemCount,
                    ) { position ->
                        val deal = dealsPagingData[position]
                        deal?.let {
                            DealsItem(it) {
                                if (it.steamAppID!=null){
                                    navController.navigate(
                                        Routes.SteamGameDetails(
                                            steamId = it.steamAppID,
                                            dealId = it.dealID,
                                            title = it.title
                                        )
                                    )
                                }else{
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

                    when (dealsPagingData.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp), contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Text(
                                    "Something went wrong!",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                )
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }


                    item {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }*/
        }
    }
}






