package com.rkbapps.gdealz.util

import androidx.compose.runtime.key

/*

  SortDealsBy.time: '-time',
  SortDealsBy.cut: '-cut',
  SortDealsBy.price: 'price',
  SortDealsBy.releasedate: '-release-date',
  SortDealsBy.rank: 'rank',
  SortDealsBy.steamplayers: '-steam-players',
  SortDealsBy.steamreviews: '-steam-reviews',
  SortDealsBy.metacritic: '-metacritic',
  SortDealsBy.metacriticuser: '-metacritic-user',


 */
enum class IsThereAnyDealSortingOptions(
    value:String,
    key:String
) {
    TRENDING(value = "Trending", key="-trending"),
    NEWEST(value="Newest", key = "-time"),
    HIGHEST_PRICE_CUT(value = "Highest Price Cut", key = "-cut"),
    LOWEST_PRICE(value = "Lowest Price",key="price"),
    RELEASE_DATE(value = "Release Date", key = "-release-date"),
    MOST_POPULAR(value = "Most Popular", key = "rank"),
    METACRITIC_SCORE("Metacritic Score","-metacritic"),
    METACRITIC_USER_SCORE("Metacritic User Score","-metacritic-user")


}