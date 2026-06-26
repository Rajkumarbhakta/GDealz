package com.rkbapps.gdealz.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.GiveawaysDao
import com.rkbapps.gdealz.db.dao.StoreDao
import com.rkbapps.gdealz.db.entity.FavDeals
import com.rkbapps.gdealz.db.entity.Store
import com.rkbapps.gdealz.models.Giveaway

@Database(entities = [Store::class,FavDeals::class, Giveaway::class], version = 4, exportSchema = false)
abstract class GDatabase:RoomDatabase() {
    abstract fun storeDao():StoreDao
    abstract fun favDealsDao():FavDealsDao
    abstract fun giveawaysDao(): GiveawaysDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new column with default value for existing rows
        try {
            db.execSQL("ALTER TABLE giveaways ADD COLUMN isClaimed INTEGER NOT NULL DEFAULT 0")
        }catch (_: Exception){}
        try {
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN steamAppId TEXT")
        }catch (_: Exception){
        }
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN actualPrice REAL")
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN currentlyLowestPrice REAL")
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN discountPercentage REAL")
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN currencySymbol TEXT")
        } catch (_: Exception) {}
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE `fav-deals` ADD COLUMN slug TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}

        db.execSQL(
            """
            UPDATE `fav-deals`
            SET slug = gameID,
                gameID = ''
            WHERE gameID IS NOT NULL
              AND gameID != ''
            """.trimIndent()
        )
    }
}
