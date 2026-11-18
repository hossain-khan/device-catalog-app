package dev.hossain.devicecatalog.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * External-content FTS4 virtual table backed by [AndroidDeviceEntity] (`device`)
 * for fast multi-token prefix search across manufacturer, model name, brand, and codename.
 */
@Fts4(contentEntity = AndroidDeviceEntity::class)
@Entity(tableName = "device_fts")
data class DeviceFtsEntity(
    val manufacturer: String,
    @ColumnInfo(name = "model_name")
    val modelName: String,
    val brand: String,
    val device: String,
)
