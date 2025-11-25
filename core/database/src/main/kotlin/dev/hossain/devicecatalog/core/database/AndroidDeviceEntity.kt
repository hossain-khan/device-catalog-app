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
)
