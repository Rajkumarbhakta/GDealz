package com.rkbapps.gdealz.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = context.packageName)

    companion object{
        val IS_USE_SYSTEM_THEME = booleanPreferencesKey("is_use_system_theme")
        val IS_DARK_THEME = booleanPreferencesKey("is_use_dark_theme")
        val SELECTED_COUNTRY = stringPreferencesKey("selected_country")
        val IS_NSFW_ALLOWED = booleanPreferencesKey("is_nsfw_allowed")
    }



    suspend fun <T> saveObject(key: Preferences.Key<String>, value: T) {
        val json = gson.toJson(value)
        context.dataStore.edit { preferences ->
            preferences[key] = json
        }
    }

    suspend fun <T> getObjectSynchronous(key: Preferences.Key<String>, classOfT: Class<T>): T? {
        val json = try {
            context.dataStore.data.first()[key]
        }catch (e: Exception){
            null
        }
        return gson.fromJson(json, classOfT)
    }


    fun <T>getObject(key: Preferences.Key<String>, classOfT: Class<T>): Flow<T> = context.dataStore.data
        .catch {emit(emptyPreferences())}
        .map {
            val json = it[key]
            gson.fromJson(json, classOfT)
        }





    fun getBooleanPreference(key:Preferences.Key<Boolean>,defaultValue: Boolean = false) = context.dataStore.data
        .catch {emit(emptyPreferences())}
        .map {
            it[key]?:defaultValue
        }

    suspend fun saveBooleanPreference(key:Preferences.Key<Boolean>, value:Boolean){
        context.dataStore.edit {preferences->
            preferences[key] = value
        }
    }

    fun getLongPreference(key:Preferences.Key<Long>, defaultValue: Long? = 0) = context.dataStore.data
        .catch {emit(emptyPreferences())}
        .map {
            it[key]?:defaultValue
        }


    suspend fun getLongPreferenceSynchronous(
        key: Preferences.Key<Long>,
        defaultValue: Long? = 0
    ): Long? {
        return try {
            context.dataStore.data.first()[key] ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    suspend fun saveLongPreference(key:Preferences.Key<Long>, value:Long){
        context.dataStore.edit {preferences->
            preferences[key] = value
        }
    }


    fun getIntPreference(key:Preferences.Key<Int>, defaultValue: Int? = 0) = context.dataStore.data
        .catch {emit(emptyPreferences())}
        .map {
            it[key]?:defaultValue
        }
    suspend fun saveIntPreference(key:Preferences.Key<Int>, value:Int){
        context.dataStore.edit {preferences->
            preferences[key] = value
        }
    }
    fun getStringPreference(key:Preferences.Key<String>, defaultValue: String? = null) = context.dataStore.data
        .catch {emit(emptyPreferences())}
        .map {
            it[key]?:defaultValue
        }
    suspend fun saveStringPreference(key:Preferences.Key<String>, value:String){
        context.dataStore.edit {preferences->
            preferences[key] = value
        }
    }


    

    /**
     * Clears all preferences stored in the DataStore.
     * This will remove all stored data, including user authentication status,
     * last selected project ID, and any other saved preferences. Use this with caution.
     * Should be called from a coroutine due to the suspend modifier.
     */
    suspend fun clearPreferences() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }

    
    /**
     * Clears the preference value associated with the given key from DataStore.
     * This is useful for deleting specific preferences without removing all data.
     * Should be called from a coroutine due to the suspend modifier.
     *
     * @param key The preference key whose data needs to be removed.
     */
    suspend fun <T>clearPreference(key:Preferences.Key<T>){
        context.dataStore.edit {preferences-> preferences.remove(key) }
    }



}