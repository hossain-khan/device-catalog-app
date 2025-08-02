package dev.hossain.devicecatalog.db

import androidx.room.Embedded
import androidx.room.Relation
import dev.hossain.android.catalogparser.models.AndroidDevice

/**
 * Data class representing an Android device with all its related entities.
 * This follows the Room one-to-many relationship pattern as described in:
 * https://developer.android.com/training/data-storage/room/relationships/one-to-many
 */
data class AndroidDeviceWithRelations(
    @Embedded val device: AndroidDeviceEntity,

    @Relation(
        parentColumn = "_id",
        entityColumn = "device_id"
    )
    val abis: List<DeviceAbi>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "device_id"
    )
    val openGlVersions: List<DeviceOpenGl>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "device_id"
    )
    val screenDensities: List<DeviceScreenDensity>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "device_id"
    )
    val screenSizes: List<DeviceScreenSize>,

    @Relation(
        parentColumn = "_id",
        entityColumn = "device_id"
    )
    val sdkVersions: List<DeviceSdkVersion>
) {
    /**
     * Converts this entity with relations to the domain model.
     */
    fun toModel(): AndroidDevice = AndroidDevice(
        brand = device.brand,
        device = device.device,
        manufacturer = device.manufacturer,
        modelName = device.modelName,
        ram = device.ram,
        formFactor = device.formFactor,
        processorName = device.processorName,
        gpu = device.gpu,
        screenSizes = screenSizes.map { it.screen_size },
        screenDensities = screenDensities.map { it.screen_density },
        abis = abis.map { it.abi },
        sdkVersions = sdkVersions.map { it.sdk_version },
        openGlEsVersions = openGlVersions.map { it.opengl_version }
    )

    companion object {
        /**
         * Creates an AndroidDeviceWithRelations from a domain model.
         * Note: This creates entities with device_id = 0, which should be updated after
         * the main device entity is inserted and its ID is known.
         */
        fun fromModel(model: AndroidDevice, deviceId: Long = 0): AndroidDeviceWithRelations {
            return AndroidDeviceWithRelations(
                device = AndroidDeviceEntity(
                    id = deviceId,
                    brand = model.brand,
                    device = model.device,
                    manufacturer = model.manufacturer,
                    modelName = model.modelName,
                    ram = model.ram,
                    formFactor = model.formFactor,
                    processorName = model.processorName,
                    gpu = model.gpu
                ),
                abis = model.abis.map { DeviceAbi(device_id = deviceId, abi = it) },
                openGlVersions = model.openGlEsVersions.map { DeviceOpenGl(device_id = deviceId, opengl_version = it) },
                screenDensities = model.screenDensities.map { DeviceScreenDensity(device_id = deviceId, screen_density = it) },
                screenSizes = model.screenSizes.map { DeviceScreenSize(device_id = deviceId, screen_size = it) },
                sdkVersions = model.sdkVersions.map { DeviceSdkVersion(device_id = deviceId, sdk_version = it) }
            )
        }
    }
}
