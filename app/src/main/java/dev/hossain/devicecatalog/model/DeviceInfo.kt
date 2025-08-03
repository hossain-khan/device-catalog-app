package dev.hossain.devicecatalog.model

import dev.hossain.android.catalogparser.models.AndroidDevice

/**
 * Represents a device with its associated information.
 *
 * @property id Unique identifier for the device.
 * @property androidDevice The Android device details.
 */
data class DeviceInfo(
    val id: Long,
    val androidDevice: AndroidDevice,
)
