package dev.hossain.devicecatalog.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "device_screen_size",
    primaryKeys = ["device_id", "screen_size"],
    foreignKeys = [
        ForeignKey(
            entity = AndroidDeviceEntity::class,
            parentColumns = ["_id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["device_id"])],
)
data class DeviceScreenSize(
    val device_id: Long,
    val screen_size: String,
)
