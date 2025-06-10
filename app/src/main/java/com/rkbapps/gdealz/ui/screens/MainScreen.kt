package com.rkbapps.gdealz.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.gdealz.navigation.BottomNavigationItem
import com.rkbapps.gdealz.navigation.BottomNavigationNavGraph
import com.rkbapps.gdealz.ui.composables.BottomNavigationBar


@Composable
fun MainScreen(navController: NavHostController){
    val navigationItems = remember { listOf(
            BottomNavigationItem.Deals,
            BottomNavigationItem.Free,
            BottomNavigationItem.Search,
            BottomNavigationItem.Fav
        ) }
    val context = LocalContext.current

    val navController = rememberNavController()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }
    )


    LaunchedEffect(Unit) {
        askPermission(context, launcher)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(items = navigationItems, navController = navController)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding(),
                start = it.calculateStartPadding(LayoutDirection.Ltr),
                end = it.calculateStartPadding(LayoutDirection.Ltr)
            ),
        ) {
            BottomNavigationNavGraph(
                navController = navController,
            )
        }
    }
}

fun askPermission(context: Context,launcher: ActivityResultLauncher<String>){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(permission)
        }
    }
}