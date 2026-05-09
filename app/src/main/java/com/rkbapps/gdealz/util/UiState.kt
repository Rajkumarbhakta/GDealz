package com.rkbapps.gdealz.util

data class UiState<T>(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: T? = null,
)

sealed interface Resource<T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val message: String) : Resource<T>
}
