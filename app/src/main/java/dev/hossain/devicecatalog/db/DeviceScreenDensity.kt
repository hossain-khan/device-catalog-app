package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "device_screen_density",
    primaryKeys = ["device_id", "screen_density"],
    foreignKeys = [
        ForeignKey(
            entity = AndroidDeviceEntity::class,
            parentColumns = ["_id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["device_id"])]
)
data class DeviceScreenDensity(
    val device_id: Long,
    val screen_density: Int
)