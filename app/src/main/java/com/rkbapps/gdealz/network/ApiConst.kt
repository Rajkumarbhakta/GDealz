package com.rkbapps.gdealz.network

import android.util.Log
import com.rkbapps.gdealz.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ApiConst {
    const val CHEAP_SHARK_BASE_URL = "https://www.cheapshark.com/api/1.0/"
    const val BASE_URL_GAME_POWER = "https://www.gamerpower.com/api/"
    private const val REDIRECT_URL = "https://www.cheapshark.com/redirect"
    const val IMAGE_URL = "https://www.cheapshark.com"
    const val  STEAM_URL = "https://store.steampowered.com/api/"

    const val IS_THERE_ANY_DEAL_BASE_URL = "https://api.isthereanydeal.com"
    const val IS_THERE_ANY_DEAL_API_KEY = BuildConfig.API_KEY

    fun redirect(dealId: String): String {
        return "$REDIRECT_URL?dealID=$dealId"
    }
    fun getFormattedDate(
        timestamp: Long?,
        format: String = "MMM dd, yyyy",
        //timeZone: TimeZone = TimeZone.getDefault()
    ): String? {
        Log.d("TAG", "getFormattedDate: $timestamp")
        if (timestamp==null || timestamp <= 0) {
            return null // Return null if timestamp is invalid
        }
        return try {
            val date = Date(timestamp * 1000L)
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            //sdf.timeZone = timeZone
            sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFormattedDate(dateString: String,format:String ="yyyy-MM-dd HH:mm:ss" ): String? {
        return try {
            val inputFormat = SimpleDateFormat(format, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            date?.let { outputFormat.format(it) } // Safely format if date is not null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




}


enum class ShortingOptions(val option: String) {
    Rating(option = "DealRating"),
    Title(option = "Title"),
    Savings(option = "Savings"),
    Price(option = "Price"),
    Reviews(option = "Reviews"),
    Release(option = "Release"),
//    Store(option = "Store"),
//    Recent(option = "Recent")
}

object GiveawayPlatforms {
    const val PC = "pc"
    const val STEAM = "steam"
    const val EPIC = "epic-games-store"
    const val UBISOFT = "ubisoft"
    const val GOG = "gog"
    const val ANDROID = "android"
    const val IOS = "ios"
    const val PS4 = "ps4.ps5"
    const val XBOX = "xbox-one.xbox-series-xs.xbox-360"
    /*
*
* eg: pc, steam, epic-games-store, ubisoft, gog, itchio, ps4, ps5, xbox-one, xbox-series-xs,
*  switch, android, ios, vr, battlenet, origin, drm-free, xbox-360
* */
}

object GiveawaySortingOptions {
    const val DATE = "date"
    const val VALUE = "value"
    const val POPULARITY = "popularity"

    /*
    date, value, popularity
     */

}
