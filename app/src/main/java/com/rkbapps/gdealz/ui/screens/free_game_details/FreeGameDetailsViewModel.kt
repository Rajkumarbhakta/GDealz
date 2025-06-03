package com.rkbapps.gdealz.ui.screens.free_game_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject


@HiltViewModel
class FreeGameDetailsViewModel @Inject constructor(
    private val repository: FreeGamesDetailsRepository,
    gson: Gson,
    saveStateHandle: SavedStateHandle
): ViewModel() {

    private val deal = saveStateHandle.toRoute<Routes.FreeGameDetails>()
    val giveaway: Giveaway? = gson.fromJson(deal.giveaway,Giveaway::class.java)


}