package com.rkbapps.gdealz.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rkbapps.gdealz.network.ApiConst
import com.rkbapps.gdealz.network.ApiConst.CHEAP_SHARK_BASE_URL
import com.rkbapps.gdealz.network.ApiConst.BASE_URL_GAME_POWER
import com.rkbapps.gdealz.network.api.CheapSharkApi
import com.rkbapps.gdealz.network.api.GamePowerApi
import com.rkbapps.gdealz.network.api.IsThereAnyDealApi
import com.rkbapps.gdealz.network.api.SteamApi
import com.rkbapps.gdealz.util.hasNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
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
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .cache(myCache)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor {chain ->
                var request = chain.request()
                val hasNetwork = context.hasNetwork() == true
                request = if (hasNetwork)
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                else
                    request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                chain.proceed(request)
            }
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
            .baseUrl(CHEAP_SHARK_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): CheapSharkApi {
        return retrofit.create(CheapSharkApi::class.java)
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

    @Provides
    @Singleton
    fun provideIsThereAnyDealApi(client: OkHttpClient): IsThereAnyDealApi{
        return Retrofit.Builder()
            .baseUrl(ApiConst.IS_THERE_ANY_DEAL_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IsThereAnyDealApi::class.java)
    }


}