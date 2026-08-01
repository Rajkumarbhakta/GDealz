package com.rkbapps.gdealz.ui.tab.deals

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.IsThereAnyDealFilters
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.tab.deals.composables.DealsItemShimmer
import com.rkbapps.gdealz.ui.tab.deals.composables.FilterBottomSheet
import com.rkbapps.gdealz.ui.tab.deals.composables.IsThereAnyDealDealsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@SuppressLint("ConfigurationScreenWidthHeight", "UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsTab(navController: NavHostController, viewModel: DealsTabViewModel = hiltViewModel()) {

    val filter by viewModel.isThereAnyDealFilter.collectAsStateWithLifecycle()
    val country by viewModel.country.collectAsStateWithLifecycle()
    val favStoreIds by viewModel.favStoreIds.collectAsStateWithLifecycle()

    val isThereAnyDealPager = viewModel.isThereAnyDeals.collectAsLazyPagingItems()

    val isFilterDialogVisible = remember { mutableStateOf(false) }


    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }


    val isChooseCountryDialogOpen = remember { mutableStateOf(false) }


    val defaultFilter = remember { IsThereAnyDealFilters() }

    LaunchedEffect(country) {
        delay(500.milliseconds)
        if (country == null) {
            isChooseCountryDialogOpen.value = true
        }
    }

        Scaffold (
            topBar = {
                CommonTopBar(
                    title = stringResource(R.string.app_name),
                    actions = {
                        if (filter != defaultFilter) {
                            Button(
                                onClick = { viewModel.clearIsThereAnyDealFilter() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(stringResource(R.string.clear_filter))
                            }
                        }
                    })
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            // open country choose dialog.
            if (isChooseCountryDialogOpen.value) {
                Dialog(onDismissRequest = {}) {
                    ChooseCountryDialog(modifier = Modifier.height(500.dp)) {
                        viewModel.updateCountry(it.key)
                        isChooseCountryDialogOpen.value = false
                    }
                }
            }

            // open filter bottom-sheet
            if (showBottomSheet.value) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxWidth().padding(top = innerPadding.calculateTopPadding()),
                    onDismissRequest = { showBottomSheet.value = false },
                    sheetState = sheetState,
                    contentWindowInsets = { WindowInsets(top = 0.dp) }
                ) {
                    FilterBottomSheet(
                        appliedFilters = filter,
                        favStoreIds = favStoreIds?.ids
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
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    ),
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
                            text = stringResource(R.string.fresh_deals),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.W400,
                        )
                        Text(stringResource(R.string.hot_deals_subtitle))
                    }
                    IconButton(onClick = {
                        isFilterDialogVisible.value = true
                        showBottomSheet.value = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = "filter deals"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    when (isThereAnyDealPager.loadState.refresh) {
                        is LoadState.Loading -> {
                            items(10) {
                                DealsItemShimmer()
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                val error =
                                    remember { isThereAnyDealPager.loadState.refresh as LoadState.Error }
                                ErrorScreen(error.error.message ?: stringResource(R.string.error_occurred))
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }

                    if (isThereAnyDealPager.itemCount <= 0 && !isThereAnyDealPager.loadState.hasError) {
                        item {
                            ErrorScreen(stringResource(R.string.no_deals_found))
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
                                    stringResource(R.string.error_occurred),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                )
                            }
                        }
                        is LoadState.NotLoading -> {}
                    }
                }
            }
        }

}