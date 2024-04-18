package com.rkbapps.gdealz.ui.tab.free

import com.rkbapps.gdealz.models.Giveaway

data class FreeGamesUiState(
    val data:List<Giveaway> = emptyList(),
    val isLoading :Boolean = false,
    val userMessage:String? = null
)