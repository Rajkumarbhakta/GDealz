package com.rkbapps.gdealz.ui.tab.home.repository

import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.models.Deals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class HomeTabRepository @Inject constructor(private val api:ApiInterface) {
    private val _deals = MutableStateFlow<List<Deals>>(emptyList())

    val deals:StateFlow<List<Deals>> = _deals

    suspend fun getAllDeals(){
        try {
            val response = api.getAllDeals()
            if (response.isSuccessful){
                _deals.emit(response.body()!!)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    suspend fun getDealsByFilter(storeId:Int,upperPrice:Int){
        try {
            val response = api.getDeals(storeId, upperPrice)
            if (response.isSuccessful){
                _deals.emit(response.body()!!)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }





}