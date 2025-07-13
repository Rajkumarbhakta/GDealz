package com.rkbapps.gdealz.util

import androidx.annotation.DrawableRes
import com.rkbapps.gdealz.R

object StoreUtil {
    fun getStoreImage(storeId: Int): Int? {
        return storeList.find { it.id == storeId }?.image
    }
    fun getStore(storeId: Int): Store? {
        return storeList.find { it.id == storeId }
    }
    fun getStores() = storeList
}

data class Store(
    val id:Int,
    val name:String,
    @DrawableRes
    val image: Int
)

private val storeList = listOf(
    Store(19, "2game", R.drawable.two_game),
    Store(2, "AllYouPlay", R.drawable.all_your_play),
    Store(4, "Blizzard", R.drawable.blizzard),
    Store(13, "DLGamer", R.drawable.dl_gamer),
    Store(15, "Dreamgame", R.drawable.dream_games),
    Store(52, "EA Store", R.drawable.ea),
    Store(16, "Epic Game Store", R.drawable.epic_store),
    Store(6, "Fanatical", R.drawable.fanatical),
    Store(17, "FireFlower", R.drawable.fireflower),
    Store(20, "GameBillet", R.drawable.gamebillet),
    Store(68, "Gamer Thor", R.drawable.game_thor),
    Store(24, "GamersGate", R.drawable.gamers_gate),
    Store(25, "Gamesload", R.drawable.games_load),
    Store(27, "GamesPlanet DE", R.drawable.gamers_planet),
    Store(28, "GamesPlanet FR", R.drawable.gamers_planet),
    Store(26, "GamesPlanet UK", R.drawable.gamers_planet),
    Store(29, "GamesPlanet US", R.drawable.gamers_planet),
    Store(35, "GOG", R.drawable.gog),
    Store(36, "GreenManGaming", R.drawable.green_man_gaming),
    Store(37, "Humble Store", R.drawable.humble),
    Store(42, "IndieGala Store", R.drawable.indigala),
    Store(65, "JoyBuggy", R.drawable.joybuggy),
    Store(47, "MacGameStore", R.drawable.mac_game_store),
    Store(48, "Microsoft Store", R.drawable.microsoft_store),
    Store(49, "Newegg", R.drawable.newegg_logo),
    Store(66, "Noctre", R.drawable.noctre),
    Store(50, "Nuuvem", R.drawable.nuuvem),
    Store(73, "PlanetPlay", R.drawable.planet_play),
    Store(70, "Playsum", R.drawable.playsum),
    Store(61, "Steam", R.drawable.steam),
    Store(62, "Ubisoft Store", R.drawable.ubisoft_store),
    Store(64, "WinGameStore", R.drawable.wingamestore),
    Store(72, "ZOOM Platform", R.drawable.zoomplatformwhite),
)
