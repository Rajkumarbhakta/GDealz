package com.rkbapps.gdealz.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

sealed class NetworkResponse<out T> {
    data class Success<T>(val value: T) : NetworkResponse<T>()
    sealed class Error : NetworkResponse<Nothing>() {
        data class HttpError(val errorCode: Int, val error: Exception) : Error()
        data object NetworkError : Error()
        data object UnknownError : Error()
    }
}


suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResponse<T> = withContext(Dispatchers.IO) {
    return@withContext try {
        NetworkResponse.Success(apiCall())
    } catch (e: HttpException) {
        NetworkResponse.Error.HttpError(e.code(), e)
    } catch (_: UnknownHostException) {
        NetworkResponse.Error.NetworkError
    } catch (e: Exception) {
        Log.d("PagingSource", "UnknownError: ${e.message}")
        NetworkResponse.Error.UnknownError
    }
}