package dev.hossain.devicecatalog.core.common

import android.content.Context
import dev.zacsweers.metro.Inject

// Service class to retrieve application version information
@Inject
class AppVersionService
    constructor(
        context: Context,
    ) {
        private val versionName: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

        fun getApplicationVersion(): String = versionName
    }
