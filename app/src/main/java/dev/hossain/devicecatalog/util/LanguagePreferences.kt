package dev.hossain.devicecatalog.util

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.hossain.devicecatalog.prefs.PreferenceKeys
import timber.log.Timber
import java.util.Locale

/**
 * Manages per-app language preferences.
 * Uses AndroidX support for Android 13+ and legacy systems.
 */
object LanguagePreferences {
    private const val PREF_LANGUAGE = "app_language"

    /**
     * Sets the app language.
     * On Android 13+, uses the system API.
     * On older versions, uses AppCompat's locale setting.
     *
     * @param languageTag IETF BCP 47 language tag (e.g., "en", "es", "fr")
     */
    fun setLanguage(
        context: Context,
        languageTag: String,
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setLanguageAndroid13(context, languageTag)
            } else {
                setLanguageLegacy(languageTag)
            }
            saveLanguagePreference(context, languageTag)
            Timber.d("App language set to: $languageTag")
        } catch (e: Exception) {
            Timber.e(e, "Failed to set app language to: $languageTag")
        }
    }

    /**
     * Gets the current app language.
     */
    fun getCurrentLanguage(context: Context): String? {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        return prefs.getString(PREF_LANGUAGE, null)
    }

    /**
     * Resets to system default language.
     */
    fun resetToSystemDefault(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resetLanguageAndroid13(context)
            } else {
                resetLanguageLegacy()
            }
            removeLanguagePreference(context)
            Timber.d("App language reset to system default")
        } catch (e: Exception) {
            Timber.e(e, "Failed to reset app language")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setLanguageAndroid13(
        context: Context,
        languageTag: String,
    ) {
        val localeList = LocaleList(Locale.forLanguageTag(languageTag))
        context
            .getSystemService(android.app.LocaleManager::class.java)
            .applicationLocales = localeList
    }

    private fun setLanguageLegacy(languageTag: String) {
        val locale = Locale.forLanguageTag(languageTag)
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun resetLanguageAndroid13(context: Context) {
        context
            .getSystemService(android.app.LocaleManager::class.java)
            .applicationLocales = LocaleList.getEmptyLocaleList()
    }

    private fun resetLanguageLegacy() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }

    private fun saveLanguagePreference(
        context: Context,
        languageTag: String,
    ) {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANGUAGE, languageTag).apply()
    }

    private fun removeLanguagePreference(context: Context) {
        val prefs = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().remove(PREF_LANGUAGE).apply()
    }

    /**
     * Gets a list of supported languages.
     * Add more languages as needed.
     */
    fun getSupportedLanguages(): List<LanguageOption> =
        listOf(
            LanguageOption("en", "English"),
            LanguageOption("es", "Español"),
            LanguageOption("fr", "Français"),
            LanguageOption("de", "Deutsch"),
            LanguageOption("ja", "日本語"),
            LanguageOption("ko", "한국어"),
            LanguageOption("zh", "中文"),
        )

    data class LanguageOption(
        val tag: String,
        val displayName: String,
    )
}
