package com.rkbapps.gdealz.ui.screens.game_info

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.db.PreferenceManager
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.models.game_info.GameInfo
import com.rkbapps.gdealz.models.price.Deals
import com.rkbapps.gdealz.models.price.PriceDetail
import com.rkbapps.gdealz.models.price.PriceHistoryEntry
import com.rkbapps.gdealz.network.NetworkResponse
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.safeApiCall
import com.rkbapps.gdealz.ui.tab.fav.FavDealsState
import com.rkbapps.gdealz.util.UiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GameInfoRepository @Inject constructor (
    @ApplicationContext private val context: Context,
    private val apiService: IsThereAnyDealApi,
    private val favDealsDao: FavDealsDao,
    private val perfManager: PreferenceManager
) {
    
    private val _gameInfo = MutableStateFlow(UiState<GameInfo>())
    val gameInfo = _gameInfo.asStateFlow()

    private val _gamePriceInfo = MutableStateFlow(UiState<PriceDetail>())
    val gamePriceInfo = _gamePriceInfo.asStateFlow()

    private val _priceHistory = MutableStateFlow(UiState<List<PriceHistoryEntry>>())
    val priceHistory = _priceHistory.asStateFlow()

    private val isFav = mutableStateOf(false)
    val isFavDeal: State<Boolean> = isFav

    private val _dealFavStatus = MutableStateFlow<FavDealsState>(FavDealsState())
    val dealFavStatus: StateFlow<FavDealsState> = _dealFavStatus
    
    
    suspend fun getGameInfo(gameId: String){
        _gameInfo.value = UiState(isLoading = true)
        when(val response = safeApiCall { apiService.getGameInfo(gameId = gameId) }){
            is NetworkResponse.Error.HttpError -> {
                _gameInfo.value = UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }
            NetworkResponse.Error.NetworkError -> {
                _gameInfo.value = UiState(error = context.getString(R.string.unable_to_connect))
            }
            NetworkResponse.Error.UnknownError -> {
                _gameInfo.value = UiState(error = context.getString(R.string.something_went_wrong_try_again))
            }
            is NetworkResponse.Success<GameInfo> -> {
                _gameInfo.value = UiState(data = response.value)
            }
        }
    }


    /** [dealsOnly] leaves out stores selling at full price. */
    suspend fun getGamePriceInfo(gameId: String, dealsOnly: Boolean = true){
        _gamePriceInfo.value = UiState(isLoading = true)

        val country = perfManager.getStringPreferenceSynchronous(PreferenceManager.SELECTED_COUNTRY,"US")?:"US"

        when(val response = safeApiCall { apiService.getPrices(
            country = country,
            deals = dealsOnly,
            gameIds = listOf(gameId)) }){
            is NetworkResponse.Error.HttpError -> {
                _gamePriceInfo.value = UiState(error = context.getString(R.string.error_code_message, response.errorCode, response.error.localizedMessage))
            }
            NetworkResponse.Error.NetworkError -> {
                _gamePriceInfo.value = UiState(error = context.getString(R.string.unable_to_connect))
            }
            NetworkResponse.Error.UnknownError -> {
                _gamePriceInfo.value = UiState(error = context.getString(R.string.something_went_wrong_try_again))
            }
            is NetworkResponse.Success<List<PriceDetail>> -> {
                val data = response.value
                if (data.isNotEmpty()){
                    _gamePriceInfo.value = UiState(data = data.first())
                }else{
                    _gamePriceInfo.value = UiState(error = context.getString(R.string.no_data_found))
                }
            }
        }

    }


    /** Price changes across all stores over the last 12 months (the API defaults to 3). */
    suspend fun getPriceHistory(gameId: String) {
        _priceHistory.value = UiState(isLoading = true)

        val country = perfManager.getStringPreferenceSynchronous(PreferenceManager.SELECTED_COUNTRY,"US")?:"US"
        val since = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365)))

        when (val response = safeApiCall { apiService.getPriceHistory(gameId = gameId, country = country, since = since) }) {
            is NetworkResponse.Error -> {
                _priceHistory.value = UiState(error = context.getString(R.string.something_went_wrong_try_again))
            }
            is NetworkResponse.Success<List<PriceHistoryEntry>> -> {
                _priceHistory.value = UiState(data = response.value)
            }
        }
    }


    suspend fun markFavDeals(gameInfo: GameInfo,prices: List<Deals>) {
        try {
            if (isFavDeal.value) {
                removeFromFav(gameInfo.id)
            } else {
                markDealsInFav(gameInfo,prices)
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

    private suspend fun markDealsInFav(gameInfo: GameInfo,prices: List<Deals>) {
        try {
            val cheapDeal = prices.minByOrNull { it.price?.amount?:Double.MAX_VALUE }
            favDealsDao.insertFavDeals(
                FavDeals(
                    dealID = gameInfo.id,
                    gameID = gameInfo.id,
                    slug = gameInfo.slug ?: "",
                    thumb = gameInfo.assets?.boxart,
                    title = gameInfo.title,
                    steamAppId = gameInfo.steamAppId?.toString(),
                    actualPrice = cheapDeal?.regular?.amount,
                    currentlyLowestPrice = cheapDeal?.price?.amount,
                    discountPercentage = cheapDeal?.cut?.toDouble(),
                    currencySymbol = cheapDeal?.price?.currency
                )
            )
            isFav.value = true
            _dealFavStatus.emit(FavDealsState(true, context.getString(R.string.added_to_favourites)))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.failed_to_add_to_favourites)))
            e.printStackTrace()
        }

    }

    private suspend fun removeFromFav(dealId: String) {
        try {
            val deal = favDealsDao.findByDealID(dealID = dealId)
            favDealsDao.deleteFavDeals(deal)
            isFav.value = false
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.removed_from_favourites)))
        } catch (e: Exception) {
            _dealFavStatus.emit(FavDealsState(false, context.getString(R.string.failed_to_remove_from_favourites)))
            e.printStackTrace()
        }
    }
    
    
    
}