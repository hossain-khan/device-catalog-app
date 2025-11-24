package dev.hossain.devicecatalog.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.hossain.devicecatalog.R
import timber.log.Timber

/**
 * Manages notification channels for the application.
 * Notification channels are required for Android 8.0 (API level 26) and higher.
 *
 * Channels:
 * - SYNC: For device catalog sync updates
 */
object NotificationChannels {
    const val CHANNEL_SYNC = "device_sync"

    /**
     * Creates all notification channels required by the app.
     * Safe to call multiple times - will update existing channels.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createSyncChannel(context)
            Timber.d("Notification channels created")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSyncChannel(context: Context) {
        val name = context.getString(R.string.notification_channel_sync_name)
        val descriptionText = context.getString(R.string.notification_channel_sync_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(CHANNEL_SYNC, name, importance).apply {
                description = descriptionText
                enableLights(false)
                enableVibration(false)
            }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Creates a notification builder for the sync channel.
     *
     * **Important**: Caller must set required notification properties before building:
     * - Small icon via `setSmallIcon()`
     * - Content title via `setContentTitle()`
     * - Content text via `setContentText()`
     *
     * Example usage:
     * ```kotlin
     * val notification = NotificationChannels.createSyncNotificationBuilder(context)
     *     .setSmallIcon(R.drawable.ic_notification)
     *     .setContentTitle("Sync Complete")
     *     .setContentText("Device catalog updated")
     *     .build()
     * ```
     */
    fun createSyncNotificationBuilder(context: Context): NotificationCompat.Builder =
        NotificationCompat
            .Builder(context, CHANNEL_SYNC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
}
