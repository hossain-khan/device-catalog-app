package dev.hossain.devicecatalog.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the weather alert app.
 * Mainly contains the cities with coordinates.
 *
 * - https://developer.android.com/training/data-storage/room
 */
@Database(
    entities = [AndroidDeviceEntity::class],
    version = 1,
    exportSchema = true,
    // https://developer.android.com/training/data-storage/room/migrating-db-versions
    // https://github.com/hossain-khan/android-weather-alert/issues/272#issuecomment-2629512823
    // https://medium.com/androiddevelopers/room-auto-migrations-d5370b0ca6eb
    autoMigrations = [],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun androidDeviceDao(): AndroidDeviceDao
}
