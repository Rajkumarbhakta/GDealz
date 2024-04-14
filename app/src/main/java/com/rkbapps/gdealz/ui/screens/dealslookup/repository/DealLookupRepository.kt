package com.rkbapps.gdealz.ui.screens.dealslookup.repository

import android.util.Log
import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.models.DealsInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DealLookupRepository @Inject constructor(private val api:ApiInterface) {
    private val _dealsData = MutableStateFlow<DealsInfo>(DealsInfo())
    val gameData :StateFlow<DealsInfo> = _dealsData

    suspend fun getDealsInfo(id:String){
        try {

            val response = api.getDealsInfo(id = id)
//            val response = api.getDealsInfo()
//            Log.d("DEALSLOOKUP", "${response.raw()}")
            if (response.isSuccessful){
                response.raw()
                _dealsData.emit(response.body()!!)
//                Log.d("DEALSLOOKUP", "${ response.body() }")
            }else{
                Log.d("DEALSLOOKUP", "${ response.errorBody()?.string() }")
            }
        }catch (e:Exception){
            e.printStackTrace()
            Log.e("DEALSLOOKUP", "Error",e)
        }
    }






}