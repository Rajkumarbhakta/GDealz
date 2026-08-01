package com.rkbapps.gdealz.ui.tab.settings

import android.os.Build
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.ui.composables.ChooseCountryDialog
import com.rkbapps.gdealz.ui.composables.CommonFilledIconButton
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.util.AppLocaleManager
import com.rkbapps.gdealz.util.Country
import com.rkbapps.gdealz.util.appLanguages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val isNsfw by viewModel.isNsfw.collectAsStateWithLifecycle()
    val isDynamicColor by viewModel.isDynamicTheme.collectAsStateWithLifecycle()

    val selectedCountryEnum = remember(selectedCountry) {
        if (selectedCountry == null) Country.US else Country.valueOf(selectedCountry!!)
    }

    var isChooseCountryDialogOpen by remember { mutableStateOf(false) }
    var isLanguageDialogOpen by remember { mutableStateOf(false) }

    val currentLanguageCode = AppLocaleManager.getLanguageCode(context = context)


    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(R.string.settings),
            )
        }
    ) { innerPadding ->


        if (isChooseCountryDialogOpen){
            Dialog(
                onDismissRequest = { isChooseCountryDialogOpen=false }
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

        if (isLanguageDialogOpen) {
            Dialog(
                onDismissRequest = { isLanguageDialogOpen = false }
            ) {
                LanguageSelectionDialog(
                    modifier = Modifier.height(500.dp),
                    currentLanguageCode = currentLanguageCode
                ) { languageCode ->
                    viewModel.changeLanguage(languageCode)
                    isLanguageDialogOpen = false
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

            item(key="top header") {
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
                                Text(stringResource(R.string.raise_a_issue))
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
                                Text(stringResource(R.string.buy_me_a_coffee))
                            }
                        }

                    }
                }
            }

            item(key="country") {
                ChooseCountryItem(
                    selectedCountry = selectedCountryEnum
                ) { isChooseCountryDialogOpen = true }
            }

            item(key="lang") {
                LanguageItem(
                    currentLanguageCode = currentLanguageCode
                ) {
                    isLanguageDialogOpen = true
                }
            }

            item(key="theme") {
                TextWithSwitch(
                    text = stringResource(R.string.follow_system_theme),
                    subText = stringResource(R.string.follow_system_theme_desc),
                    checked = isSystemTheme,
                    icon = Icons.Default.BrightnessAuto
                ) {
                    viewModel.updateIsSystemTheme(it)
                }
            }
            item(key="dark theme") {
                AnimatedVisibility(!isSystemTheme) {
                    TextWithSwitch(
                        text = stringResource(R.string.dark_theme),
                        checked = isDarkTheme,
                        icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode
                    ) {
                        viewModel.updateTheme(it)
                    }
                }
            }
            item(key="dynamic color") {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    TextWithSwitch(
                        text = stringResource(R.string.dynamic_color),
                        subText = stringResource(R.string.dynamic_color_desc),
                        checked = isDynamicColor,
                        icon = ImageVector.vectorResource(R.drawable.palette)
                    ) {
                        viewModel.updateDynamicTheme(it)
                    }
                }
            }
            item(key="nsfw content") {
                TextWithSwitch(
                    text = stringResource(R.string.nsfw_content),
                    subText = stringResource(R.string.nsfw_content_desc),
                    checked = isNsfw,
                    icon = Icons.Default.Block
                ) {
                    viewModel.updateNsfwContentAllowance(it)
                }
            }

            item(key="privacy policy") {
                TextWithArrow(
                    text = stringResource(R.string.privacy_policy),
                    subText = stringResource(R.string.privacy_policy_desc),
                    icon = Icons.Outlined.PrivacyTip
                ) {
                    uriHandler.openUri("https://sites.google.com/view/gdealz/home")
                }
            }
            item(key="disclaimer") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.disclaimer),
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
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.country),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(R.string.country_desc),
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

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    currentLanguageCode: String,
    onClick: () -> Unit
) {
    val languageName = when (currentLanguageCode) {
        "en" -> stringResource(R.string.english)
        "ru" -> stringResource(R.string.russian)
        "hi" -> stringResource(R.string.hindi)
        "de" -> stringResource(R.string.german)
        "fr" -> stringResource(R.string.french)
        "ja" -> stringResource(R.string.japanese)
        "ko" -> stringResource(R.string.korean)
        "bn" -> stringResource(R.string.bengali)
        else -> stringResource(R.string.english)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            "language",
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(R.string.language_description),
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
                "$languageName \uD83D\uDD3D",
                modifier = Modifier.padding(8.dp)
            )
        }

    }
}

@Composable
fun LanguageSelectionDialog(
    modifier: Modifier = Modifier,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    var selectedLanguageCode by remember { mutableStateOf(currentLanguageCode) }
    var searchQuery by remember { mutableStateOf("") }
    var languageList by remember { mutableStateOf(appLanguages) }

    LaunchedEffect(searchQuery) {
        languageList = if (searchQuery.isNotEmpty() && searchQuery.isNotBlank()) {
            appLanguages.filter {
                it.displayLanguage.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        } else {
            appLanguages
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            stringResource(R.string.select_language),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(stringResource(R.string.search_here)) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(100.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(languageList) { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguageCode = language.code },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguageCode == language.code,
                        onClick = { selectedLanguageCode = language.code }
                    )
                    Text(language.displayLanguage)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onLanguageSelected(selectedLanguageCode) }
        ) {
            Text(stringResource(R.string.confirm))
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