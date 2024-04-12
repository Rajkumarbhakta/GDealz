package com.rkbapps.gdealz.ui.screens.dealslookup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.rkbapps.gdealz.R

class DealLookupScreen(private val delaId:String?,private val title:String?):Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text(text = title?:stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                )
            },
        ){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it))
            {

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp,
                    vertical = 8.dp)){




                }






            }
        }
    }
}