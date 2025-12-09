package dev.hossain.devicecatalog.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manages onboarding state for first-time users.
 * Uses DataStore Preferences API for modern, type-safe data storage.
 *
 * Note: DataStore operations are asynchronous. Use the Flow API for reactive updates
 * or call suspend functions within a coroutine scope.
 */
object OnboardingManager {
    // Extension property to create DataStore instance
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PreferenceKeys.APP_PREFERENCES,
    )

    // Preference key for onboarding completion status
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey(PreferenceKeys.ONBOARDING_COMPLETED)

    /**
     * Returns a Flow that emits the onboarding completion status.
     * Use this for reactive updates in Composables.
     *
     * @param context Android context for accessing DataStore
     * @return Flow<Boolean> that emits true if onboarding has been completed, false otherwise
     */
    fun onboardingCompletedFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }

    /**
     * Checks if the user has completed onboarding.
     * This is a suspend function that returns the current value.
     *
     * @param context Android context for accessing DataStore
     * @return true if onboarding has been completed, false otherwise
     */
    suspend fun hasCompletedOnboarding(context: Context): Boolean =
        context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }.first()

    /**
     * Marks onboarding as completed.
     * This is a suspend function that should be called within a coroutine scope.
     *
     * @param context Android context for accessing DataStore
     */
    suspend fun markOnboardingCompleted(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    /**
     * Resets onboarding state (useful for testing).
     * This is a suspend function that should be called within a coroutine scope.
     *
     * @param context Android context for accessing DataStore
     */
    suspend fun resetOnboarding(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = false
        }
    }
}
