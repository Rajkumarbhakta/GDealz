package com.rkbapps.gdealz.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.Giveaway

@Database(entities = [Store::class,FavDeals::class, Giveaway::class], version = 1, exportSchema = false)
abstract class GDatabase:RoomDatabase() {
    abstract fun storeDao():StoreDao
    abstract fun favDealsDao():FavDealsDao

    abstract fun giveawaysDao(): GiveawaysDao
}