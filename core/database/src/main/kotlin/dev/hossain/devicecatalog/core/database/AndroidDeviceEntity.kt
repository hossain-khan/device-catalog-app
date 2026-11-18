package dev.hossain.devicecatalog.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Main device entity with indexes on frequently queried columns for optimal performance.
 * Indexes added for:
 * - manufacturer: Used in search and filtering
 * - model_name: Used in search queries
 * - brand: Used in device property lookups
 * - form_factor: Used in filtering operations
 */
@Entity(
    tableName = "device",
    indices = [
        Index(value = ["manufacturer"]),
        Index(value = ["model_name"]),
        Index(value = ["brand"]),
        Index(value = ["form_factor"]),
        Index(value = ["min_sdk"]),
        Index(value = ["max_sdk"]),
        Index(value = ["ram_min_mb"]),
        Index(value = ["ram_max_mb"]),
        Index(
            value = ["sort_score", "manufacturer", "model_name"],
            orders = [Index.Order.DESC, Index.Order.ASC, Index.Order.ASC],
        ),
    ],
)
data class AndroidDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val brand: String,
    val device: String,
    val manufacturer: String,
    @ColumnInfo(name = "model_name")
    val modelName: String,
    val ram: String,
    @ColumnInfo(name = "form_factor")
    val formFactor: String,
    @ColumnInfo(name = "processor_name")
    val processorName: String,
    val gpu: String,
    @ColumnInfo(name = "min_sdk", defaultValue = "0")
    val minSdk: Int = 0,
    @ColumnInfo(name = "max_sdk", defaultValue = "0")
    val maxSdk: Int = 0,
    @ColumnInfo(name = "ram_min_mb", defaultValue = "0")
    val ramMinMb: Int = 0,
    @ColumnInfo(name = "ram_max_mb", defaultValue = "0")
    val ramMaxMb: Int = 0,
    @ColumnInfo(name = "sort_score", defaultValue = "0")
    val sortScore: Int = 0,
)
