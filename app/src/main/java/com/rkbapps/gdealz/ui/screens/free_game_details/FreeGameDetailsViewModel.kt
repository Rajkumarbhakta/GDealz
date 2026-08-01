package com.rkbapps.gdealz.ui.screens.free_game_details

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.navigation.Routes
import com.rkbapps.gdealz.network.api.GamePowerApi
import com.rkbapps.gdealz.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class FreeGameDetailsViewModel @Inject constructor(
    private val repository: FreeGamesDetailsRepository,
    private val saveStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val _giveaway = MutableStateFlow(UiState<Giveaway>(isLoading = true))
    val giveaway = _giveaway.asStateFlow()

    init {
        viewModelScope.launch {
            val deal = saveStateHandle.toRoute<Routes.FreeGameDetails>()
            getGiveaway(deal.giveawayId)
        }
    }

    suspend fun getGiveaway(id: Int) {
        _giveaway.value = UiState(isLoading = true)
        try {
            val data = repository.getGiveaway(id)
            _giveaway.value = UiState(data = data)
        } catch (e: Exception) {
            _giveaway.value = UiState(error = e.localizedMessage)
        }
    }

    fun markGiveawayAsClaimed(giveaway: Giveaway) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markGiveawayAsClaimed(giveaway)
        }
    }

    fun markGiveawayAsUnClaimed(giveaway: Giveaway) = viewModelScope.launch(Dispatchers.IO) {
        repository.markGiveawayAsUnClaimed(giveaway)
    }

    fun sendNotification(){
        viewModelScope.launch {
            repository.sendNotification(context = context)
        }
    }



}