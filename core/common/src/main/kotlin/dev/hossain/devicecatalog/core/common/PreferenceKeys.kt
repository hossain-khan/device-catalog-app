package dev.hossain.devicecatalog.core.common

/**
 * Constants for DataStore preference file names used throughout the app.
 * Centralizes preference file names to ensure consistency and prevent typos.
 *
 * Note: The app uses DataStore Preferences API instead of SharedPreferences.
 * @see androidx.datastore.preferences.preferencesDataStore
 */
object PreferenceKeys {
    /**
     * General app preferences (UI settings, user preferences, etc.)
     */
    const val APP_PREFERENCES = "app_preferences"

    /**
     * Feature flags for gradual rollouts and A/B testing
     */
    const val FEATURE_FLAGS = "feature_flags"

    /**
     * Secure preferences file name (reserved for future use with encrypted storage)
     */
    const val SECURE_PREFERENCES = "secure_preferences"

    /**
     * Key for storing onboarding completion status
     */
    const val ONBOARDING_COMPLETED = "onboarding_completed"
}
