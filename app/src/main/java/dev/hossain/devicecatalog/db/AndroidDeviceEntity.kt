package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "android_devices")
data class AndroidDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val device: String,
    val manufacturer: String,
    val modelName: String,
    val ram: String,
    val formFactor: String,
    val processorName: String,
    val gpu: String,
)
