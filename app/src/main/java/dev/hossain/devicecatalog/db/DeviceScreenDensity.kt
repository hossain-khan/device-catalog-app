package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "device_screen_density",
    primaryKeys = ["device_id", "screen_density"],
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
data class DeviceScreenDensity(
    val device_id: Long,
    val screen_density: Int
)