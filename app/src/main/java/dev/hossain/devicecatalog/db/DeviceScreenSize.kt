package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "device_screen_size",
    primaryKeys = ["device_id", "screen_size"],
    foreignKeys = [
        ForeignKey(
            entity = AndroidDeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DeviceScreenSize(
    val device_id: Long,
    val screen_size: String
)