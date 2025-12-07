package dev.hossain.devicecatalog.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Test to verify the Material You color scheme implementation with green seed color.
 */
class ColorThemeTest {
    @Test
    fun `verify primary colors are configured`() {
        // Test that primary colors are properly defined for light theme
        assertEquals("primaryLight should be green", Color(0xFF4C662B), primaryLight)
        assertEquals("onPrimaryLight should be white", Color(0xFFFFFFFF), onPrimaryLight)
        assertEquals("primaryContainerLight", Color(0xFFCDEDA3), primaryContainerLight)
        assertEquals("onPrimaryContainerLight", Color(0xFF354E16), onPrimaryContainerLight)

        // Test primary colors for dark theme
        assertEquals("primaryDark", Color(0xFFB1D18A), primaryDark)
        assertEquals("onPrimaryDark", Color(0xFF1F3701), onPrimaryDark)
        assertEquals("primaryContainerDark", Color(0xFF354E16), primaryContainerDark)
        assertEquals("onPrimaryContainerDark", Color(0xFFCDEDA3), onPrimaryContainerDark)
    }

    @Test
    fun `verify secondary colors are configured`() {
        // Test secondary colors
        assertEquals("secondaryLight should be green", Color(0xFF4C662B), secondaryLight)
        assertEquals("secondaryDark", Color(0xFFB1D18A), secondaryDark)
    }

    @Test
    fun `verify tertiary colors are configured`() {
        // Test tertiary colors (teal/cyan)
        assertEquals("tertiaryLight should be teal", Color(0xFF006A66), tertiaryLight)
        assertEquals("tertiaryDark should be light teal", Color(0xFF80D5CF), tertiaryDark)
    }

    @Test
    fun `verify surface colors are distinct`() {
        // Ensure light and dark surface colors are different
        assertNotEquals("surfaceLight and surfaceDark should differ", surfaceLight, surfaceDark)
        assertNotEquals("backgroundLight and backgroundDark should differ", backgroundLight, backgroundDark)
    }

    @Test
    fun `verify error colors are configured`() {
        // Test error colors
        assertEquals("errorLight", Color(0xFFBA1A1A), errorLight)
        assertEquals("errorDark", Color(0xFFFFB4AB), errorDark)
    }
}
