package com.example.data

import android.content.Context
import android.os.Build
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
    val colorPalette: String = "OCEAN",
    val isAmoledMode: Boolean = false,
    val isProfileCreated: Boolean = false,
    val profileUsername: String = "",
    val profilePassword: String = ""
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
        val KEY_AMOLED_MODE = booleanPreferencesKey("amoled_mode")
        val KEY_IS_PROFILE_CREATED = booleanPreferencesKey("is_profile_created")
        val KEY_PROFILE_USERNAME = stringPreferencesKey("profile_username")
        val KEY_PROFILE_PASSWORD = stringPreferencesKey("profile_password")

        private val VALID_THEME_MODES = setOf("SYSTEM", "LIGHT", "DARK")
        private val VALID_PALETTES = setOf("DYNAMIC", "OCEAN", "SAKURA", "MACHA", "ESPRESSO", "LAVENDER")
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
            val isAmoledMode = preferences[KEY_AMOLED_MODE] ?: false
            val isProfileCreated = preferences[KEY_IS_PROFILE_CREATED] ?: false
            val profileUsername = preferences[KEY_PROFILE_USERNAME].orEmpty()
            val profilePassword = preferences[KEY_PROFILE_PASSWORD].orEmpty()

            var rawThemeMode = preferences[KEY_THEME_MODE] ?: "DARK"
            if (rawThemeMode !in VALID_THEME_MODES) {
                rawThemeMode = "DARK"
            }

            var rawPalette = preferences[KEY_COLOR_PALETTE] ?: "OCEAN"
            if (rawPalette !in VALID_PALETTES) {
                rawPalette = "OCEAN"
            }

            // API 31 alti ise DYNAMIC palet OCEAN'a düşer
            if (rawPalette == "DYNAMIC" && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                rawPalette = "OCEAN"
            }

            UserPreferences(
                phoneNumber = phoneNumber,
                isOnboardingCompleted = isOnboardingCompleted,
                isAutoConnectEnabled = isAutoConnectEnabled,
                updateChannel = updateChannel,
                checkUpdatesOnLaunch = checkUpdatesOnLaunch,
                latestStableVersion = latestStableVersion,
                latestBetaVersion = latestBetaVersion,
                themeMode = rawThemeMode,
                colorPalette = rawPalette,
                isAmoledMode = isAmoledMode,
                isProfileCreated = isProfileCreated,
                profileUsername = profileUsername,
                profilePassword = profilePassword
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
        val safeMode = if (mode in VALID_THEME_MODES) mode else "DARK"
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = safeMode
        }
    }

    suspend fun setColorPalette(palette: String) {
        var safePalette = if (palette in VALID_PALETTES) palette else "OCEAN"
        if (safePalette == "DYNAMIC" && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            safePalette = "OCEAN"
        }
        context.dataStore.edit { preferences ->
            preferences[KEY_COLOR_PALETTE] = safePalette
        }
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AMOLED_MODE] = enabled
        }
    }

    suspend fun saveConnectionProfile(username: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_PROFILE_CREATED] = true
            preferences[KEY_PROFILE_USERNAME] = username
            preferences[KEY_PROFILE_PASSWORD] = token
        }
    }

    suspend fun clearConnectionProfile() {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_PROFILE_CREATED] = false
            preferences[KEY_PROFILE_USERNAME] = ""
            preferences[KEY_PROFILE_PASSWORD] = ""
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
