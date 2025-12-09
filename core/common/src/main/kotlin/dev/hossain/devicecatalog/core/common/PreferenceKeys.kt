package dev.hossain.devicecatalog.core.common

/**
 * Constants for SharedPreferences file names used throughout the app.
 * Centralizes preference file names to ensure consistency and prevent typos.
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
     * Secure preferences file name (used with EncryptedSharedPreferences)
     */
    const val SECURE_PREFERENCES = "secure_preferences"

    /**
     * Key for storing onboarding completion status
     */
    const val ONBOARDING_COMPLETED = "onboarding_completed"
}
