package com.rkbapps.gdealz.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.composables.CommonFilledIconButton
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current

    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val isNsfw by viewModel.isNsfw.collectAsStateWithLifecycle()

    val selectedCountryEnum = remember(selectedCountry) {
        if (selectedCountry == null) Country.US else Country.valueOf(selectedCountry!!)
    }

    var isChooseCountryDialogOpen by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Settings",
                isNavigationBack = true,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->


        if (isChooseCountryDialogOpen){
            Dialog(
                onDismissRequest = {isChooseCountryDialogOpen=false}
            ) {
                ChooseCountryDialog(
                    modifier = Modifier.height(500.dp),
                    selectedCountry = selectedCountryEnum
                ) {
                    viewModel.updateCountry(it.key)
                    isChooseCountryDialogOpen=false
                }
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                ElevatedCard (
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                    ) {

                        Text(stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge)
                        Text("v${viewModel.appVersion}")

                        Row(modifier = Modifier.padding(vertical = 10.dp)) {
                            CommonFilledIconButton(
                                icon = ImageVector.vectorResource(R.drawable.github),
                                colors = IconButtonDefaults.filledIconButtonColors()
                            ) {
                                uriHandler.openUri("https://github.com/Rajkumarbhakta/GDealz")
                            }
                            CommonFilledIconButton(
                                icon = Icons.Default.Mail,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                uriHandler.openUri("mailto:contact@rkbapps.in")
                            }

                        }

                        HorizontalDivider()

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                uriHandler.openUri("https://github.com/Rajkumarbhakta/GDealz/issues")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.BugReport,"")
                                Text("Raise a issue")
                            }
                        }

                        Button(
                            onClick = {
                                uriHandler.openUri("https://coff.ee/rajkumarbhakta")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Icon(imageVector = Icons.Default.Coffee,"")
                                Text("Buy me a Coffee")
                            }
                        }

                    }
                }
            }

            item {
                ChooseCountryItem(
                    selectedCountry = selectedCountryEnum
                ) { isChooseCountryDialogOpen = true }
            }

            item {
                TextWithSwitch(
                    text = "Follow System Theme",
                    subText = "Use light or dark theme based on your system settings.",
                    checked = isSystemTheme,
                    icon = Icons.Default.BrightnessAuto
                ) {
                    viewModel.updateIsSystemTheme(it)
                }
            }
            item {
                AnimatedVisibility(!isSystemTheme) {
                    TextWithSwitch(
                        text = "Dark Theme",
                        checked = isDarkTheme,
                        icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode
                    ) {
                        viewModel.updateTheme(it)
                    }
                }
            }
            item {
                TextWithSwitch(
                    text = "NSFW content",
                    subText = "Displays adult or sensitive content when turned on.",
                    checked = isNsfw,
                    icon = Icons.Default.Block
                ) {
                    viewModel.updateNsfwContentAllowance(it)
                }
            }
            item {
                TextWithArrow(
                    text = "Privacy Policy",
                    subText = "Read the privacy policy",
                    icon = Icons.Outlined.PrivacyTip
                ) {
                    uriHandler.openUri("https://sites.google.com/view/gdealz/home")
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Game and pricing information is sourced from CheapShark, IsThereAnyDeal.com, and Steam. G Dealz does not guarantee the accuracy, availability, or completeness of this information.",
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }

    }


}

//


@Composable
fun TextWithSwitch(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    subText:String? = null,
    checked: Boolean = false,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            "country",
        )
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text,

                style = MaterialTheme.typography.titleLarge,
            )
            subText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}


@Composable
fun TextWithArrow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    subText:String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            "country",
        )
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text,

                style = MaterialTheme.typography.titleLarge,
            )
            subText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        IconButton(
            onClick = onClick
        ) {
            Icon(Icons.AutoMirrored.Default.ArrowForward,"")
        }
    }
}

@Composable
fun ChooseCountryItem(
    modifier: Modifier = Modifier,
    selectedCountry: Country,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            "country",
            modifier = Modifier.align(Alignment.Top).padding(top = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Country",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                "Change your country to see deals in your local currency. If deal data is not available in your currency, it will default to US Dollars ($).",
                style = MaterialTheme.typography.labelSmall
            )

        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    onClick()
                }
        ) {
            Text(
                "${selectedCountry.value} \uD83D\uDD3D",
                modifier = Modifier.padding(8.dp)
            )
        }

    }
}


@Preview
@Composable
fun SettingsPreview() {
    GDealzTheme {
        SettingsScreen(rememberNavController())
    }
}