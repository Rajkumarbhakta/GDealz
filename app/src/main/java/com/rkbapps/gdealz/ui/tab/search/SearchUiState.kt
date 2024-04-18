package com.rkbapps.gdealz.ui.tab.search

import com.rkbapps.gdealz.models.Game


data class SearchUiState(
    val data:List<Game> = emptyList(),
    val isLoading :Boolean = false,
    val userMessage:String? = null

)