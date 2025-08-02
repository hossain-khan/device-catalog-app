package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "device_abi",
    primaryKeys = ["device_id", "abi"],
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
data class DeviceAbi(
    val device_id: Long,
    val abi: String
)