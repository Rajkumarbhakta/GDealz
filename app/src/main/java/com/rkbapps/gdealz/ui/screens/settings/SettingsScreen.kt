package com.rkbapps.gdealz.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.Country

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val isNsfw by viewModel.isNsfw.collectAsStateWithLifecycle()

    val selectedCountryEnum = remember(selectedCountry) {
        if (selectedCountry == null) Country.US else Country.valueOf(selectedCountry!!)
    }


    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Settings",
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Select Country",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Text(
                            "${selectedCountryEnum.value} \uD83D\uDD3D",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            item {
                TextWithSwitch(text = "Follow System Theme", checked = isSystemTheme) {
                    viewModel.updateIsSystemTheme(it)
                }
            }

            item {
                AnimatedVisibility(!isSystemTheme) {
                    TextWithSwitch(text = "Dark Theme", checked = isDarkTheme) {
                        viewModel.updateTheme(it)
                    }
                }
            }
            item {
                TextWithSwitch(text = "Allow NSFW content", checked = isNsfw) {
                    viewModel.updateNsfwContentAllowance(it)
                }
            }


        }

    }


}


@Composable
fun TextWithSwitch(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean = false,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}


@Preview
@Composable
fun SettingsPreview() {
    GDealzTheme {
        SettingsScreen(rememberNavController())
    }
}