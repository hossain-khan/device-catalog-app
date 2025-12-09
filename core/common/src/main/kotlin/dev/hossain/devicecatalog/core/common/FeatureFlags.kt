package dev.hossain.devicecatalog.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.hossain.devicecatalog.core.common.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Feature flags for gradual rollouts and A/B testing.
 * Uses DataStore Preferences API for modern, type-safe data storage.
 *
 * Note: DataStore operations are asynchronous. Use the Flow API for reactive updates
 * or call suspend functions within a coroutine scope.
 */
object FeatureFlags {
    // Extension property to create DataStore instance
    private val Context.featureFlagsDataStore: DataStore<Preferences> by preferencesDataStore(
        name = PreferenceKeys.FEATURE_FLAGS,
    )

    // Feature flag keys
    private const val KEY_HAPTIC_FEEDBACK = "feature_haptic_feedback"
    private const val KEY_ADVANCED_STATISTICS = "feature_advanced_statistics"
    private const val KEY_DEVICE_COMPARISON = "feature_device_comparison"
    private const val KEY_EXPORT_PDF = "feature_export_pdf"
    private const val KEY_DEEP_LINKING = "feature_deep_linking"

    // DataStore preference keys
    private val HAPTIC_FEEDBACK_KEY = booleanPreferencesKey(KEY_HAPTIC_FEEDBACK)
    private val ADVANCED_STATISTICS_KEY = booleanPreferencesKey(KEY_ADVANCED_STATISTICS)
    private val DEVICE_COMPARISON_KEY = booleanPreferencesKey(KEY_DEVICE_COMPARISON)
    private val EXPORT_PDF_KEY = booleanPreferencesKey(KEY_EXPORT_PDF)
    private val DEEP_LINKING_KEY = booleanPreferencesKey(KEY_DEEP_LINKING)

    // Map of key names to preference keys for efficient lookup
    private val keyNameToPreferenceKey =
        mapOf(
            KEY_HAPTIC_FEEDBACK to HAPTIC_FEEDBACK_KEY,
            KEY_ADVANCED_STATISTICS to ADVANCED_STATISTICS_KEY,
            KEY_DEVICE_COMPARISON to DEVICE_COMPARISON_KEY,
            KEY_EXPORT_PDF to EXPORT_PDF_KEY,
            KEY_DEEP_LINKING to DEEP_LINKING_KEY,
        )

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
     * Returns a Flow that emits haptic feedback enabled status.
     * Use this for reactive updates in Composables.
     */
    fun isHapticFeedbackEnabledFlow(context: Context): Flow<Boolean> =
        getFlagFlow(context, HAPTIC_FEEDBACK_KEY, KEY_HAPTIC_FEEDBACK)

    /**
     * Returns a Flow that emits advanced statistics enabled status.
     * Use this for reactive updates in Composables.
     */
    fun isAdvancedStatisticsEnabledFlow(context: Context): Flow<Boolean> =
        getFlagFlow(context, ADVANCED_STATISTICS_KEY, KEY_ADVANCED_STATISTICS)

    /**
     * Returns a Flow that emits device comparison enabled status.
     * Use this for reactive updates in Composables.
     */
    fun isDeviceComparisonEnabledFlow(context: Context): Flow<Boolean> =
        getFlagFlow(context, DEVICE_COMPARISON_KEY, KEY_DEVICE_COMPARISON)

    /**
     * Returns a Flow that emits PDF export enabled status.
     * Use this for reactive updates in Composables.
     */
    fun isPdfExportEnabledFlow(context: Context): Flow<Boolean> =
        getFlagFlow(context, EXPORT_PDF_KEY, KEY_EXPORT_PDF)

    /**
     * Returns a Flow that emits deep linking enabled status.
     * Use this for reactive updates in Composables.
     */
    fun isDeepLinkingEnabledFlow(context: Context): Flow<Boolean> =
        getFlagFlow(context, DEEP_LINKING_KEY, KEY_DEEP_LINKING)

    /**
     * Checks if haptic feedback is enabled.
     * This is a suspend function that returns the current value.
     */
    suspend fun isHapticFeedbackEnabled(context: Context): Boolean =
        getFlag(context, HAPTIC_FEEDBACK_KEY, KEY_HAPTIC_FEEDBACK)

    /**
     * Checks if advanced statistics are enabled.
     * This is a suspend function that returns the current value.
     */
    suspend fun isAdvancedStatisticsEnabled(context: Context): Boolean =
        getFlag(context, ADVANCED_STATISTICS_KEY, KEY_ADVANCED_STATISTICS)

    /**
     * Checks if device comparison is enabled.
     * This is a suspend function that returns the current value.
     */
    suspend fun isDeviceComparisonEnabled(context: Context): Boolean =
        getFlag(context, DEVICE_COMPARISON_KEY, KEY_DEVICE_COMPARISON)

    /**
     * Checks if PDF export is enabled.
     * This is a suspend function that returns the current value.
     */
    suspend fun isPdfExportEnabled(context: Context): Boolean =
        getFlag(context, EXPORT_PDF_KEY, KEY_EXPORT_PDF)

    /**
     * Checks if deep linking is enabled.
     * This is a suspend function that returns the current value.
     */
    suspend fun isDeepLinkingEnabled(context: Context): Boolean =
        getFlag(context, DEEP_LINKING_KEY, KEY_DEEP_LINKING)

    /**
     * Returns a Flow that emits a feature flag value from DataStore.
     */
    private fun getFlagFlow(
        context: Context,
        key: Preferences.Key<Boolean>,
        keyName: String,
    ): Flow<Boolean> =
        context.featureFlagsDataStore.data.map { preferences ->
            val defaultValue = defaultFlags[keyName] ?: false
            val value = preferences[key] ?: defaultValue
            Timber.v("Feature flag $keyName: $value")
            value
        }

    /**
     * Gets a feature flag value from DataStore.
     * This is a suspend function that returns the current value.
     */
    private suspend fun getFlag(
        context: Context,
        key: Preferences.Key<Boolean>,
        keyName: String,
    ): Boolean = getFlagFlow(context, key, keyName).first()

    /**
     * Sets a feature flag value (for testing and debugging).
     * This is a suspend function that should be called within a coroutine scope.
     */
    suspend fun setFlag(
        context: Context,
        key: String,
        value: Boolean,
    ) {
        // Use the cached preference key if available, otherwise create a new one
        val preferenceKey =
            keyNameToPreferenceKey[key]
                ?: run {
                    Timber.w("Unknown feature flag key: $key. Creating dynamic preference key.")
                    booleanPreferencesKey(key)
                }
        context.featureFlagsDataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
        Timber.d("Feature flag $key set to: $value")
    }

    /**
     * Gets all feature flags for display in developer settings.
     * This is a suspend function that returns the current values.
     */
    suspend fun getAllFlags(context: Context): Map<String, Boolean> =
        context.featureFlagsDataStore.data.map { preferences ->
            defaultFlags.mapValues { (key, defaultValue) ->
                val preferenceKey =
                    keyNameToPreferenceKey[key]
                        ?: run {
                            Timber.w("Unknown feature flag key in defaultFlags: $key. Creating dynamic preference key.")
                            booleanPreferencesKey(key)
                        }
                preferences[preferenceKey] ?: defaultValue
            }
        }.first()

    /**
     * Resets all flags to their default values.
     * This is a suspend function that should be called within a coroutine scope.
     */
    suspend fun resetToDefaults(context: Context) {
        context.featureFlagsDataStore.edit { preferences ->
            preferences.clear()
        }
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
