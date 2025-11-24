package dev.hossain.devicecatalog.model

import androidx.compose.runtime.Immutable
import dev.hossain.android.catalogparser.models.AndroidDevice

/**
 * Represents a device with its associated information.
 *
 * @property id Unique identifier for the device.
 * @property androidDevice The Android device details.
 *
 * Performance: Marked as @Immutable to help Compose skip unnecessary recompositions.
 * This tells Compose that once created, this object's properties will never change.
 */
@Immutable
data class DeviceInfo(
    val id: Long,
    val androidDevice: AndroidDevice,
)
