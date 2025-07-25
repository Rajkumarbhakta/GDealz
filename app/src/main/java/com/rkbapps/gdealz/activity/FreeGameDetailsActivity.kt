package com.rkbapps.gdealz.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.ui.composables.CommonTopBar
import com.rkbapps.gdealz.ui.screens.free_game_details.composable.FreeGameDetailsBody
import com.rkbapps.gdealz.ui.theme.GDealzTheme
import com.rkbapps.gdealz.worker.NotificationWorkerRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FreeGameDetailsActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val viewModel: FreeGameDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val gameId = intent.getIntExtra(NotificationWorkerRepository.GAME_GIVEAWAY_ID, 0)

        viewModel.getGiveaway(gameId)

        val isSystemTheme = preferenceManager.getBooleanPreference(
            PreferenceManager.Companion.IS_USE_SYSTEM_THEME,
            true
        )
            .stateIn(
                lifecycleScope,
                SharingStarted.Companion.Lazily,
                true
            )

        val isDarkTheme =
            preferenceManager.getBooleanPreference(PreferenceManager.Companion.IS_DARK_THEME, false)
                .stateIn(
                    lifecycleScope,
                    SharingStarted.Companion.Lazily,
                    false
                )

        setContent {

            val isSystemTheme by isSystemTheme.collectAsStateWithLifecycle()
            val darkTheme by isDarkTheme.collectAsStateWithLifecycle()

            GDealzTheme(
                darkTheme = if (isSystemTheme) isSystemInDarkTheme() else darkTheme
            ) {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val giveaway by viewModel.giveaway.collectAsStateWithLifecycle()
                    val uriHandler = LocalUriHandler.current

                    Scaffold(
                        topBar = {
                            CommonTopBar(
                                title = giveaway?.title ?: "",
                                isNavigationBack = false,
                            )
                        },
                        bottomBar = {
                            Box(
                                modifier = Modifier.padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        giveaway?.let { viewModel.markGiveawayAsClaimed(it) }
                                        giveaway?.openGiveawayUrl?.let {
                                            uriHandler.openUri(it)
                                        }
                                    }
                                ) {
                                    Text(if (giveaway?.isClaimed == true) "Mark as un claimed" else "Grab Deal")
                                }
                            }
                        }
                    ) { innerPadding ->
                        FreeGameDetailsBody(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding), giveaway = giveaway
                        )
                    }
                }
            }
        }
    }
}


@HiltViewModel
class FreeGameDetailsViewModel @Inject constructor(
    private val giveawaysDao: GiveawaysDao
) : ViewModel() {

    private val _giveaway = MutableStateFlow<Giveaway?>(null)
    val giveaway = _giveaway.asStateFlow()

    fun getGiveaway(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_giveaway.value != null) {
                return@launch
            }
            _giveaway.value = giveawaysDao.getGiveawayById(id)
        }
    }

    fun markGiveawayAsClaimed(giveaway: Giveaway) {
        viewModelScope.launch(Dispatchers.IO) {
            val update = giveaway.copy(isClaimed = true)
            giveawaysDao.updateGiveaway(update)
        }
    }


}