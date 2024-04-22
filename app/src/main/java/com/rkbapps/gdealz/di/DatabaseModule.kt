package com.rkbapps.gdealz.di

import android.content.Context
import androidx.room.Room
import com.rkbapps.gdealz.db.GDatabase
import com.rkbapps.gdealz.db.dao.FavDealsDao
import com.rkbapps.gdealz.db.dao.StoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context:Context):GDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            GDatabase::class.java,
            "g-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStoreDao(database:GDatabase):StoreDao{
        return database.storeDao()
    }


    @Provides
    @Singleton
    fun providesFavDealsDatabase(database:GDatabase):FavDealsDao{
        return database.favDealsDao()
    }

}



