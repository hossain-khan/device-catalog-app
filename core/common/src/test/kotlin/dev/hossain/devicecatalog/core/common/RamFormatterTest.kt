package dev.hossain.devicecatalog.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [RamFormatter].
 */
class RamFormatterTest {
    @Test
    fun `formatRamToGb converts 1996MB to 2GB`() {
        assertEquals("2GB", RamFormatter.formatRamToGb("1996MB"))
    }

    @Test
    fun `formatRamToGb converts 8098MB to 8GB`() {
        assertEquals("8GB", RamFormatter.formatRamToGb("8098MB"))
    }

    @Test
    fun `formatRamToGb converts 512MB to 1GB`() {
        // 512 / 1024 = 0.5, rounds to 1
        assertEquals("1GB", RamFormatter.formatRamToGb("512MB"))
    }

    @Test
    fun `formatRamToGb converts 1024MB to 1GB`() {
        assertEquals("1GB", RamFormatter.formatRamToGb("1024MB"))
    }

    @Test
    fun `formatRamToGb converts 2048MB to 2GB`() {
        assertEquals("2GB", RamFormatter.formatRamToGb("2048MB"))
    }

    @Test
    fun `formatRamToGb converts 12288MB to 12GB`() {
        assertEquals("12GB", RamFormatter.formatRamToGb("12288MB"))
    }

    @Test
    fun `formatRamToGb converts 0MB to 0GB`() {
        assertEquals("0GB", RamFormatter.formatRamToGb("0MB"))
    }

    @Test
    fun `formatRamToGb handles 400MB correctly rounds to 0GB`() {
        // 400 / 1024 = 0.39, rounds to 0
        assertEquals("0GB", RamFormatter.formatRamToGb("400MB"))
    }

    @Test
    fun `formatRamToGb handles 600MB correctly rounds to 1GB`() {
        // 600 / 1024 = 0.59, rounds to 1
        assertEquals("1GB", RamFormatter.formatRamToGb("600MB"))
    }

    @Test
    fun `formatRamToGb handles empty string`() {
        assertEquals("", RamFormatter.formatRamToGb(""))
    }

    @Test
    fun `formatRamToGb handles blank string`() {
        assertEquals("   ", RamFormatter.formatRamToGb("   "))
    }

    @Test
    fun `formatRamToGb returns original when already in GB format`() {
        assertEquals("8GB", RamFormatter.formatRamToGb("8GB"))
    }

    @Test
    fun `formatRamToGb returns original when already in GB format lowercase`() {
        assertEquals("8gb", RamFormatter.formatRamToGb("8gb"))
    }

    @Test
    fun `formatRamToGb handles lowercase mb`() {
        assertEquals("2GB", RamFormatter.formatRamToGb("1996mb"))
    }

    @Test
    fun `formatRamToGb handles mixed case MB`() {
        assertEquals("2GB", RamFormatter.formatRamToGb("1996Mb"))
    }

    @Test
    fun `formatRamToGb handles range 1024-2048MB to 1-2GB`() {
        assertEquals("1-2GB", RamFormatter.formatRamToGb("1024-2048MB"))
    }

    @Test
    fun `formatRamToGb handles range 512-1024MB to 1-1GB`() {
        // Both round to 1GB
        assertEquals("1-1GB", RamFormatter.formatRamToGb("512-1024MB"))
    }

    @Test
    fun `formatRamToGb handles range 0-3038MB to 0-3GB`() {
        // 3038 / 1024 = 2.97, rounds to 3
        assertEquals("0-3GB", RamFormatter.formatRamToGb("0-3038MB"))
    }

    @Test
    fun `formatRamToGb handles range with spaces`() {
        assertEquals("1-2GB", RamFormatter.formatRamToGb("1024 - 2048MB"))
    }

    @Test
    fun `formatRamToGb returns original for invalid format`() {
        assertEquals("invalid", RamFormatter.formatRamToGb("invalid"))
    }

    @Test
    fun `formatRamToGb returns original for non-numeric value`() {
        assertEquals("XXMB", RamFormatter.formatRamToGb("XXMB"))
    }

    @Test
    fun `formatRamToGb returns original for invalid range format`() {
        assertEquals("1-2-3MB", RamFormatter.formatRamToGb("1-2-3MB"))
    }

    @Test
    fun `formatRamToGb handles edge case 1985MB to 2GB`() {
        // 1985 / 1024 = 1.94, rounds to 2
        assertEquals("2GB", RamFormatter.formatRamToGb("1985MB"))
    }

    @Test
    fun `formatRamToGb handles edge case 3937MB to 4GB`() {
        // 3937 / 1024 = 3.84, rounds to 4
        assertEquals("4GB", RamFormatter.formatRamToGb("3937MB"))
    }

    @Test
    fun `formatRamToGb handles edge case 12305MB to 12GB`() {
        // 12305 / 1024 = 12.02, rounds to 12
        assertEquals("12GB", RamFormatter.formatRamToGb("12305MB"))
    }

    @Test
    fun `formatRamToGb handles large range 10811-14992MB correctly`() {
        // Real data from database: 10811 / 1024 = 10.56 -> 11GB, 14992 / 1024 = 14.64 -> 15GB
        assertEquals("11-15GB", RamFormatter.formatRamToGb("10811-14992MB"))
    }

    @Test
    fun `formatRamToGb handles range 1000-1996MB correctly`() {
        // Real data from database: 1000 / 1024 = 0.98 -> 1GB, 1996 / 1024 = 1.95 -> 2GB
        assertEquals("1-2GB", RamFormatter.formatRamToGb("1000-1996MB"))
    }
}
