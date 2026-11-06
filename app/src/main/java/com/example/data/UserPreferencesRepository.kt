package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ktb_wifi_preferences")

data class UserPreferences(
    val phoneNumber: String,
    val isOnboardingCompleted: Boolean,
    val isAutoConnectEnabled: Boolean,
    val updateChannel: String = "STABLE",
    val checkUpdatesOnLaunch: Boolean = true,
    val latestStableVersion: String = "",
    val latestBetaVersion: String = "",
    val themeMode: String = "DARK",
    val colorPalette: String = "OCEAN"
)

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_PHONE_NUMBER = stringPreferencesKey("phone_number")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_AUTO_CONNECT_ENABLED = booleanPreferencesKey("auto_connect_enabled")
        val KEY_UPDATE_CHANNEL = stringPreferencesKey("update_channel")
        val KEY_CHECK_UPDATES_ON_LAUNCH = booleanPreferencesKey("check_updates_on_launch")
        val KEY_LATEST_STABLE_VERSION = stringPreferencesKey("latest_stable_version")
        val KEY_LATEST_BETA_VERSION = stringPreferencesKey("latest_beta_version")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_COLOR_PALETTE = stringPreferencesKey("color_palette")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val phoneNumber = preferences[KEY_PHONE_NUMBER].orEmpty()
            val isOnboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false
            val isAutoConnectEnabled = preferences[KEY_AUTO_CONNECT_ENABLED] ?: false
            val updateChannel = preferences[KEY_UPDATE_CHANNEL] ?: "STABLE"
            val checkUpdatesOnLaunch = preferences[KEY_CHECK_UPDATES_ON_LAUNCH] ?: true
            val latestStableVersion = preferences[KEY_LATEST_STABLE_VERSION].orEmpty()
            val latestBetaVersion = preferences[KEY_LATEST_BETA_VERSION].orEmpty()
            val themeMode = preferences[KEY_THEME_MODE] ?: "DARK"
            val colorPalette = preferences[KEY_COLOR_PALETTE] ?: "OCEAN"
            UserPreferences(
                phoneNumber = phoneNumber,
                isOnboardingCompleted = isOnboardingCompleted,
                isAutoConnectEnabled = isAutoConnectEnabled,
                updateChannel = updateChannel,
                checkUpdatesOnLaunch = checkUpdatesOnLaunch,
                latestStableVersion = latestStableVersion,
                latestBetaVersion = latestBetaVersion,
                themeMode = themeMode,
                colorPalette = colorPalette
            )
        }

    suspend fun setPhoneNumber(phoneNumber: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PHONE_NUMBER] = phoneNumber
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAutoConnectEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_CONNECT_ENABLED] = enabled
        }
    }

    suspend fun setUpdateChannel(channel: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_UPDATE_CHANNEL] = channel
        }
    }

    suspend fun setCheckUpdatesOnLaunch(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CHECK_UPDATES_ON_LAUNCH] = enabled
        }
    }

    suspend fun setLatestStableVersion(version: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LATEST_STABLE_VERSION] = version
        }
    }

    suspend fun setLatestBetaVersion(version: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LATEST_BETA_VERSION] = version
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setColorPalette(palette: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COLOR_PALETTE] = palette
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
