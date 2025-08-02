package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "device_opengl",
    primaryKeys = ["device_id", "opengl_version"],
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
data class DeviceOpenGl(
    val device_id: Long,
    val opengl_version: String
)