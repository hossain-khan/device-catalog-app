package dev.hossain.devicecatalog.core.common

import android.content.Context
import dev.hossain.devicecatalog.core.common.PreferenceKeys
import timber.log.Timber

/**
 * Feature flags for gradual rollouts and A/B testing.
 * Flags can be controlled remotely via SharedPreferences or remote config.
 */
object FeatureFlags {
    // Feature flag keys
    private const val KEY_HAPTIC_FEEDBACK = "feature_haptic_feedback"
    private const val KEY_ADVANCED_STATISTICS = "feature_advanced_statistics"
    private const val KEY_DEVICE_COMPARISON = "feature_device_comparison"
    private const val KEY_EXPORT_PDF = "feature_export_pdf"
    private const val KEY_DEEP_LINKING = "feature_deep_linking"

    // Default values
    private val defaultFlags =
        mapOf(
            KEY_HAPTIC_FEEDBACK to true,
            KEY_ADVANCED_STATISTICS to true,
            KEY_DEVICE_COMPARISON to false,
            KEY_EXPORT_PDF to false,
            KEY_DEEP_LINKING to true,
        )

    /**
     * Checks if haptic feedback is enabled.
     */
    fun isHapticFeedbackEnabled(context: Context): Boolean = getFlag(context, KEY_HAPTIC_FEEDBACK)

    /**
     * Checks if advanced statistics are enabled.
     */
    fun isAdvancedStatisticsEnabled(context: Context): Boolean = getFlag(context, KEY_ADVANCED_STATISTICS)

    /**
     * Checks if device comparison is enabled.
     */
    fun isDeviceComparisonEnabled(context: Context): Boolean = getFlag(context, KEY_DEVICE_COMPARISON)

    /**
     * Checks if PDF export is enabled.
     */
    fun isPdfExportEnabled(context: Context): Boolean = getFlag(context, KEY_EXPORT_PDF)

    /**
     * Checks if deep linking is enabled.
     */
    fun isDeepLinkingEnabled(context: Context): Boolean = getFlag(context, KEY_DEEP_LINKING)

    /**
     * Gets a feature flag value from SharedPreferences.
     */
    private fun getFlag(
        context: Context,
        key: String,
    ): Boolean {
        val prefs = context.getSharedPreferences(PreferenceKeys.FEATURE_FLAGS, Context.MODE_PRIVATE)
        val defaultValue = defaultFlags[key] ?: false
        val value = prefs.getBoolean(key, defaultValue)
        Timber.v("Feature flag $key: $value")
        return value
    }

    /**
     * Sets a feature flag value (for testing and debugging).
     */
    fun setFlag(
        context: Context,
        key: String,
        value: Boolean,
    ) {
        val prefs = context.getSharedPreferences(PreferenceKeys.FEATURE_FLAGS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
        Timber.d("Feature flag $key set to: $value")
    }

    /**
     * Gets all feature flags for display in developer settings.
     */
    fun getAllFlags(context: Context): Map<String, Boolean> {
        val prefs = context.getSharedPreferences(PreferenceKeys.FEATURE_FLAGS, Context.MODE_PRIVATE)
        return defaultFlags.mapValues { (key, defaultValue) ->
            prefs.getBoolean(key, defaultValue)
        }
    }

    /**
     * Resets all flags to their default values.
     */
    fun resetToDefaults(context: Context) {
        val prefs = context.getSharedPreferences(PreferenceKeys.FEATURE_FLAGS, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Timber.d("Feature flags reset to defaults")
    }

    /**
     * Formats a feature flag key to a human-readable display name.
     * Example: "feature_haptic_feedback" -> "Haptic Feedback"
     */
    fun formatFeatureFlagName(key: String): String =
        key
            .removePrefix("feature_")
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
}
