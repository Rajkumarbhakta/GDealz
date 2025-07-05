package com.rkbapps.gdealz.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rkbapps.gdealz.network.ApiConst
import com.rkbapps.gdealz.network.ApiConst.BASE_URL
import com.rkbapps.gdealz.network.ApiConst.BASE_URL_GAME_POWER
import com.rkbapps.gdealz.network.ApiInterface
import com.rkbapps.gdealz.network.GamePowerApi
import com.rkbapps.gdealz.network.SteamApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }


    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .serializeNulls()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
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
    fun provideGamePowerApi(client: OkHttpClient):GamePowerApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GAME_POWER)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GamePowerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSteamApi(client: OkHttpClient): SteamApi {
        return Retrofit.Builder()
            .baseUrl(ApiConst.STEAM_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SteamApi::class.java)
    }


}