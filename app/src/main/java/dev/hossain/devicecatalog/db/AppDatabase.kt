package dev.hossain.devicecatalog.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the device catalog app.
 * Contains devices and their related properties (ABIs, OpenGL, screen specs, SDK versions).
 *
 * Version 2 changes:
 * - Added indexes on frequently queried columns in device table for performance:
 *   - manufacturer (used in search and filtering)
 *   - model_name (used in search queries)
 *   - brand (used in device property lookups)
 *   - form_factor (used in filtering operations)
 *
 * - https://developer.android.com/training/data-storage/room
 */
@Database(
    entities = [
        AndroidDeviceEntity::class,
        DeviceAbi::class,
        DeviceOpenGl::class,
        DeviceScreenDensity::class,
        DeviceScreenSize::class,
        DeviceSdkVersion::class,
    ],
    version = 2,
    exportSchema = true,
    // https://developer.android.com/training/data-storage/room/migrating-db-versions
    // https://github.com/hossain-khan/android-weather-alert/issues/272#issuecomment-2629512823
    // https://medium.com/androiddevelopers/room-auto-migrations-d5370b0ca6eb
    autoMigrations = [
        androidx.room.AutoMigration(from = 1, to = 2),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun androidDeviceDao(): AndroidDeviceDao
}
