package dev.hossain.devicecatalog.core.common

import kotlin.math.roundToInt

/**
 * Utility object for formatting RAM values from megabytes (MB) to gigabytes (GB).
 *
 * This formatter converts RAM strings like "1996MB" or "8098MB" to human-readable
 * GB format like "2GB" or "8GB" by rounding to the nearest GB.
 */
object RamFormatter {
    /**
     * Regex pattern for detecting range format (e.g., "1024-2048MB").
     * Allows optional spaces around the dash.
     */
    private val RANGE_PATTERN = Regex("""(\d+)\s*-\s*(\d+)MB""", RegexOption.IGNORE_CASE)

    /**
     * Formats a RAM string from MB to GB format.
     *
     * Examples:
     * - "1996MB" -> "2GB"
     * - "8098MB" -> "8GB"
     * - "512MB" -> "1GB" (rounds up)
     * - "12288MB" -> "12GB"
     * - "0MB" -> "0GB"
     * - "" -> "" (empty string remains empty)
     * - "1GB" -> "1GB" (already in GB format)
     * - "1024-2048MB" -> "1-2GB" (range format)
     *
     * @param ramString The RAM value as a string (e.g., "1996MB", "8GB", "1024-2048MB")
     * @return The formatted RAM string in GB format, or the original string if it cannot be parsed
     */
    fun formatRamToGb(ramString: String): String {
        if (ramString.isBlank()) {
            return ramString
        }

        // Already in GB format
        if (ramString.contains("GB", ignoreCase = true)) {
            return ramString
        }

        // Handle range format (e.g., "1024-2048MB") - only match ranges with positive numbers
        // Allows optional spaces around the dash
        val rangeMatch = RANGE_PATTERN.matchEntire(ramString)
        if (rangeMatch != null) {
            return formatRamRange(rangeMatch)
        }

        // Extract numeric value from strings like "1996MB"
        val numericValue = ramString.replace("MB", "", ignoreCase = true).trim()

        return try {
            val mbValue = numericValue.toDouble()
            val gbValue = (mbValue / 1024.0).roundToInt()
            "${gbValue}GB"
        } catch (e: NumberFormatException) {
            // If parsing fails, return original string
            ramString
        }
    }

    /**
     * Formats a RAM range string from MB to GB format using regex match groups.
     *
     * Examples:
     * - "1024-2048MB" -> "1-2GB"
     * - "512-1024MB" -> "1-1GB"
     * - "0-3038MB" -> "0-3GB"
     *
     * @param matchResult The regex match result containing range boundaries
     * @return The formatted RAM range in GB format
     */
    private fun formatRamRange(matchResult: MatchResult): String {
        val (startMb, endMb) = matchResult.destructured
        return try {
            val startGb = (startMb.toDouble() / 1024.0).roundToInt()
            val endGb = (endMb.toDouble() / 1024.0).roundToInt()
            "${startGb}-${endGb}GB"
        } catch (e: NumberFormatException) {
            matchResult.value
        }
    }
}
