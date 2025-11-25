package dev.hossain.devicecatalog.core.common

import android.content.Context
import dev.zacsweers.metro.Inject

// Example service class that does not need DI module or binding
@Inject
class ExampleAppVersionService
    constructor(
        context: Context,
    ) {
        private val versionName: String = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"

        fun getApplicationVersion(): String = versionName
    }
