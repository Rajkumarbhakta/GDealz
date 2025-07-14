package com.rkbapps.gdealz.ui.screens.game_info

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.models.DealsInfo
import com.rkbapps.gdealz.models.deal.Deal
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.network.ApiConst
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.ui.screens.dealslookup.FavDealsState
import com.rkbapps.gdealz.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GameInfoRepository @Inject constructor (
    private val apiService: IsThereAnyDealApi,
    private val favDealsDao: FavDealsDao,
) {
    
    private val _gameInfo = MutableStateFlow(UiState<GameInfo>())
    val gameInfo = _gameInfo.asStateFlow()

    private val _gamePriceInfo = MutableStateFlow(UiState<PriceDetail>())
    val gamePriceInfo = _gamePriceInfo.asStateFlow()

    private val isFav = mutableStateOf(false)
    val isFavDeal: State<Boolean> = isFav

    private val _dealFavStatus = MutableStateFlow<FavDealsState>(FavDealsState())
    val dealFavStatus: StateFlow<FavDealsState> = _dealFavStatus
    
    
    suspend fun getGameInfo(gameId: String){
        _gameInfo.value = UiState(isLoading = true)
        when(val response = safeApiCall { apiService.getGameInfo(gameId = gameId) }){
            is NetworkResponse.Error.HttpError -> {
                _gameInfo.value = UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }
            NetworkResponse.Error.NetworkError -> {
                _gameInfo.value = UiState(error = "Unable to connect please check your internet connection.")
            }
            NetworkResponse.Error.UnknownError -> {
                _gameInfo.value = UiState(error = "Something went wrong! Try again later.")
            }
            is NetworkResponse.Success<GameInfo> -> {
                _gameInfo.value = UiState(data = response.value)
            }
        }
    }


    suspend fun getGamePriceInfo(gameId: String){
        _gamePriceInfo.value = UiState(isLoading = true)
        when(val response = safeApiCall { apiService.getPrices(
            country = ApiConst.COUNTRY,
            gameIds = listOf(gameId)) }){
            is NetworkResponse.Error.HttpError -> {
                _gamePriceInfo.value = UiState(error = "Code : ${response.errorCode} Error : ${response.error.localizedMessage}")
            }
            NetworkResponse.Error.NetworkError -> {
                _gamePriceInfo.value = UiState(error = "Unable to connect please check your internet connection.")
            }
            NetworkResponse.Error.UnknownError -> {
                _gamePriceInfo.value = UiState(error = "Something went wrong! Try again later.")
            }
            is NetworkResponse.Success<List<PriceDetail>> -> {
                val data = response.value
                if (data.isNotEmpty()){
                    _gamePriceInfo.value = UiState(data = data.first())
                }else{
                    _gamePriceInfo.value = UiState(error = "No data found")
                }
            }
        }

    }


    suspend fun markFavDeals(gameInfo: GameInfo,) {
        try {
            if (isFavDeal.value) {
                removeFromFav(gameInfo.id)
            } else {
                markDealsInFav(gameInfo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun isDealFav(dealId: String) {

        try {
            val isExist = favDealsDao.isExistsByDealID(dealID = dealId)
            isFav.value = isExist
        } catch (e: Exception) {
            isFav.value = false
            e.printStackTrace()
        }
    }

    private suspend fun markDealsInFav(gameInfo: GameInfo,) {
        try {
            favDealsDao.insertFavDeals(
                FavDeals(
                    dealID = gameInfo.id,
                    gameID = gameInfo.slug?:"",
                    thumb = gameInfo.assets?.boxart,
                    title = gameInfo.title,
                    steamAppId = gameInfo.steamAppId?.toString(),
                )
            )
            isFav.value = true
            _dealFavStatus.emit(FavDealsState(true, "Added to Favourites"))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, "Failed to add to Favourites"))
            e.printStackTrace()
        }

    }

    private suspend fun removeFromFav(dealId: String) {
        try {
            val deal = favDealsDao.findByDealID(dealID = dealId)
            favDealsDao.deleteFavDeals(deal)
            isFav.value = false
            _dealFavStatus.emit(FavDealsState(false, "Removed from Favourites"))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, "Failed to remove from Favourites"))
            e.printStackTrace()
        }
    }
    
    
    
}