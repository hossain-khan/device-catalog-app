package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "device_opengl",
    primaryKeys = ["device_id", "opengl_version"],
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
data class DeviceOpenGl(
    val device_id: Long,
    val opengl_version: String,
)
