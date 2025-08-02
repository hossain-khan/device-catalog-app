package dev.hossain.devicecatalog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device")
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
