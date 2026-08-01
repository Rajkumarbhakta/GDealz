package com.rkbapps.gdealz.ui.screens.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.BuildConfig
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val storeDao: StoreDao,
) : ViewModel() {

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess
    val version = BuildConfig.VERSION_NAME

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000.milliseconds)
            _isSuccess.value = true
        }
    }


}