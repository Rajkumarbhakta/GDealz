package com.rkbapps.gdealz.ui.screens.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.gdealz.BuildConfig
import com.rkbapps.gdealz.network.api.CheapSharkApi
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.safeApiCall
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
    private val apiInterface: CheapSharkApi
) : ViewModel() {

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess
    val version = BuildConfig.VERSION_NAME

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getStores()
        }
    }


    suspend fun getStores() {
        try {
            val stores = storeDao.getAllStores()
            if (stores.isEmpty()){
                val response = apiInterface.getAllStore()
                response.body()?.let { stores ->
                    stores.forEach {
                        storeDao.insertStore(Store(
                            storeID = it.storeID ?: UUID.randomUUID().toString(),
                            storeName = it.storeName ?: "",
                            banner = it.images?.banner,
                            logo = it.images?.logo,
                            icon = it.images?.icon
                        ))
                    }
                    _isSuccess.value = true
                }
            }else{
                delay(2000.milliseconds)
                _isSuccess.value = true
            }
        } catch (e: Exception) {
            delay(2000.milliseconds)
            _isSuccess.value = true
            e.printStackTrace()
        }
    }


}