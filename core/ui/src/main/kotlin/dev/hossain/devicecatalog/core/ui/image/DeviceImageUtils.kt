package dev.hossain.devicecatalog.core.ui.image

import dev.hossain.android.catalogparser.models.AndroidDevice

/**
 * Utility functions for generating device image URLs and sanitizing file name parts.
 *
 * Implements URL generation matching the hosted assets on:
 * `https://android-device-catalog.gohk.xyz/images`
 */
object DeviceImageUtils {
    const val BASE_IMAGE_URL = "https://android-device-catalog.gohk.xyz/images"

    /**
     * Sanitizes a string for use in device image filenames.
     * Replaces '/', '\', ':', '?', and ' ' with '_'.
     */
    fun sanitizeFilenamePart(part: String?): String {
        if (part.isNullOrEmpty()) return ""
        return part
            .replace('/', '_')
            .replace('\\', '_')
            .replace(':', '_')
            .replace('?', '_')
            .replace(' ', '_')
    }

    /**
     * Generates the primary device image URL using the standard format:
     * `https://android-device-catalog.gohk.xyz/images/{brand}_{device}.png`
     */
    fun getDeviceImageUrl(device: AndroidDevice): String {
        val brand = sanitizeFilenamePart(device.brand.ifBlank { device.manufacturer })
        val deviceCode = sanitizeFilenamePart(device.device)
        return "$BASE_IMAGE_URL/${brand}_$deviceCode.png"
    }

    /**
     * Generates candidate URLs for fallback image resolution, accounting for brand/manufacturer
     * casing variations, device code, and model name with both PNG and WebP extensions.
     */
    fun getDeviceImageCandidates(device: AndroidDevice): List<String> {
        val candidates = linkedSetOf<String>()

        val devCode = sanitizeFilenamePart(device.device)
        val modelName = sanitizeFilenamePart(device.modelName)

        val prefixes = listOfNotNull(
            sanitizeFilenamePart(device.brand).takeIf { it.isNotBlank() },
            sanitizeFilenamePart(device.brand.lowercase()).takeIf { it.isNotBlank() },
            sanitizeFilenamePart(device.manufacturer).takeIf { it.isNotBlank() },
            sanitizeFilenamePart(device.manufacturer.lowercase()).takeIf { it.isNotBlank() },
        ).distinct()

        for (prefix in prefixes) {
            if (devCode.isNotEmpty()) {
                candidates.add("$BASE_IMAGE_URL/${prefix}_$devCode.png")
                candidates.add("$BASE_IMAGE_URL/${prefix}_$devCode.webp")
            }
            if (modelName.isNotEmpty() && modelName != devCode) {
                candidates.add("$BASE_IMAGE_URL/${prefix}_$modelName.png")
                candidates.add("$BASE_IMAGE_URL/${prefix}_$modelName.webp")
            }
        }

        return candidates.toList()
    }
}
