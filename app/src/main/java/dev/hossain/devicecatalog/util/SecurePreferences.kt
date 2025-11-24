package dev.hossain.devicecatalog.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dev.hossain.devicecatalog.prefs.PreferenceKeys
import timber.log.Timber

/**
 * Provides secure storage for sensitive app preferences using EncryptedSharedPreferences.
 * Uses AES256 GCM for encrypting keys and values.
 *
 * For non-sensitive preferences, use standard SharedPreferences instead.
 */
class SecurePreferences(
    context: Context,
) {
    private val masterKey: MasterKey =
        MasterKey
            .Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PreferenceKeys.SECURE_PREFERENCES,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    /**
     * Stores a string value securely.
     */
    fun putString(
        key: String,
        value: String,
    ) {
        try {
            sharedPreferences.edit().putString(key, value).apply()
            Timber.d("Stored secure preference successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store secure preference")
        }
    }

    /**
     * Retrieves a string value securely.
     */
    fun getString(
        key: String,
        defaultValue: String? = null,
    ): String? =
        try {
            sharedPreferences.getString(key, defaultValue)
        } catch (e: Exception) {
            Timber.e(e, "Failed to retrieve secure preference")
            defaultValue
        }

    /**
     * Stores a boolean value securely.
     */
    fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        try {
            sharedPreferences.edit().putBoolean(key, value).apply()
            Timber.d("Stored secure preference successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to store secure preference")
        }
    }

    /**
     * Retrieves a boolean value securely.
     */
    fun getBoolean(
        key: String,
        defaultValue: Boolean = false,
    ): Boolean =
        try {
            sharedPreferences.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Timber.e(e, "Failed to retrieve secure preference")
            defaultValue
        }

    /**
     * Removes a preference.
     */
    fun remove(key: String) {
        try {
            sharedPreferences.edit().remove(key).apply()
            Timber.d("Removed secure preference successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove secure preference")
        }
    }

    /**
     * Clears all secure preferences.
     */
    fun clear() {
        try {
            sharedPreferences.edit().clear().apply()
            Timber.d("Cleared all secure preferences")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear secure preferences")
        }
    }
}
