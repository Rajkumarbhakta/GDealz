package com.rkbapps.gdealz.api

object ApiConst {
    const val BASE_URL = "https://www.cheapshark.com/api/1.0/"
    private const val REDIRECT_URL = "https://www.cheapshark.com/redirect"
    fun redirect(dealId:String):String{
        return "$REDIRECT_URL?dealID=$dealId"
    }
}

enum class ShortingOptions(val option:String){
    Rating(option = "DealRating"),
    Title(option = "Title"),
    Savings(option = "Savings"),
    Price(option = "Price"),
    Reviews(option = "Reviews"),
    Release(option = "Release"),
    Store(option = "Store"),
    Recent(option = "Recent")
}