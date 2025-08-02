package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "device_sdk",
    primaryKeys = ["device_id", "sdk_version"],
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
data class DeviceSdkVersion(
    val device_id: Long,
    val sdk_version: Int
)