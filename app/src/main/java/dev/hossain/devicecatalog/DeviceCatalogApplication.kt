package dev.hossain.devicecatalog

import android.app.Application
import timber.log.Timber

/**
 * Application class for the Device Catalog app.
 * Initializes Timber logging and other app-wide dependencies.
 */
class DeviceCatalogApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            // In debug builds, log everything to console with thread info
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "${super.createStackElementTag(element)}:${element.lineNumber}"
                }
            })
            Timber.d("Timber logging initialized for DEBUG build")
        } else {
            // In release builds, you might want to use a crash reporting tree
            // For now, we'll use a simple tree that only logs warnings and errors
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority >= android.util.Log.WARN) {
                        // In production, you might send these to crash reporting
                        // or analytics service like Firebase Crashlytics
                        android.util.Log.println(priority, tag, message)
                        t?.printStackTrace()
                    }
                }
            })
            Timber.i("Timber logging initialized for RELEASE build")
        }

        Timber.i("DeviceCatalogApplication started")
    }
}
