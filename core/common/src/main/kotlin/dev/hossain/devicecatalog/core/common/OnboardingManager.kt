package dev.hossain.devicecatalog.core.common

import android.content.Context

/**
 * Manages onboarding state for first-time users.
 * Handles storing and retrieving whether the user has completed onboarding.
 */
object OnboardingManager {
    /**
     * Checks if the user has completed onboarding.
     *
     * @param context Android context for accessing SharedPreferences
     * @return true if onboarding has been completed, false otherwise
     */
    fun hasCompletedOnboarding(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        return prefs.getBoolean(PreferenceKeys.ONBOARDING_COMPLETED, false)
    }

    /**
     * Marks onboarding as completed.
     *
     * @param context Android context for accessing SharedPreferences
     */
    fun markOnboardingCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PreferenceKeys.ONBOARDING_COMPLETED, true).apply()
    }

    /**
     * Resets onboarding state (useful for testing).
     *
     * @param context Android context for accessing SharedPreferences
     */
    fun resetOnboarding(context: Context) {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PreferenceKeys.ONBOARDING_COMPLETED, false).apply()
    }
}
