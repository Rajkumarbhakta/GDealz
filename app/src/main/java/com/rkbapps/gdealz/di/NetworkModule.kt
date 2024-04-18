package com.rkbapps.gdealz.di

import com.rkbapps.gdealz.api.ApiConst.BASE_URL
import com.rkbapps.gdealz.api.ApiConst.BASE_URL_GAME_POWER
import com.rkbapps.gdealz.api.ApiInterface
import com.rkbapps.gdealz.api.GamePowerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideGamePowerApi():GamePowerApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GAME_POWER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GamePowerApi::class.java)
    }


}