package dev.hossain.devicecatalog.core.common

import android.content.Context
import dev.zacsweers.metro.Inject

// Service class to retrieve application version information
class AppVersionService private constructor(
    private val versionName: String,
) {
    @Inject
    constructor(
        context: Context,
    ) : this(
        versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown",
    )

    fun getApplicationVersion(): String = versionName

    companion object {
        fun forTesting(versionName: String): AppVersionService = AppVersionService(versionName)
    }
}
