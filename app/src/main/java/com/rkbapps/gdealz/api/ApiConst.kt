package com.rkbapps.gdealz.api

object ApiConst {
    const val BASE_URL = "https://www.cheapshark.com/api/1.0/"
    const val BASE_URL_GAME_POWER = "https://www.gamerpower.com/api/"
    private const val REDIRECT_URL = "https://www.cheapshark.com/redirect"
    fun redirect(dealId: String): String {
        return "$REDIRECT_URL?dealID=$dealId"
    }
}


enum class ShortingOptions(val option: String) {
    Rating(option = "DealRating"),
    Title(option = "Title"),
    Savings(option = "Savings"),
    Price(option = "Price"),
    Reviews(option = "Reviews"),
    Release(option = "Release"),
    Store(option = "Store"),
    Recent(option = "Recent")
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
