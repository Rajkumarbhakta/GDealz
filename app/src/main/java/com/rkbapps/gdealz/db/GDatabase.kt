package com.rkbapps.gdealz.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.Giveaway

@Database(entities = [Store::class,FavDeals::class, Giveaway::class], version = 2, exportSchema = false)
abstract class GDatabase:RoomDatabase() {
    abstract fun storeDao():StoreDao
    abstract fun favDealsDao():FavDealsDao

    abstract fun giveawaysDao(): GiveawaysDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new column with default value for existing rows
        try {
            database.execSQL("ALTER TABLE giveaways ADD COLUMN isClaimed INTEGER NOT NULL DEFAULT 0")
        }catch (e: Exception){}
    }
}
