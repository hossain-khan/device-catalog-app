package dev.hossain.devicecatalog.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Test to verify the Developer Precision Console color scheme (Alabaster Slate & Obsidian Carbon + Android Emerald).
 */
class ColorThemeTest {
    @Test
    fun `verify primary colors are configured`() {
        // Test that primary colors are properly defined for light theme
        assertEquals("primaryLight should be emerald 600", Color(0xFF059669), primaryLight)
        assertEquals("onPrimaryLight should be white", Color(0xFFFFFFFF), onPrimaryLight)
        assertEquals("primaryContainerLight", Color(0xFFD1FAE5), primaryContainerLight)
        assertEquals("onPrimaryContainerLight", Color(0xFF065F46), onPrimaryContainerLight)

        // Test primary colors for dark theme
        assertEquals("primaryDark", Color(0xFF10B981), primaryDark)
        assertEquals("onPrimaryDark", Color(0xFF022C22), onPrimaryDark)
        assertEquals("primaryContainerDark", Color(0xFF064E3B), primaryContainerDark)
        assertEquals("onPrimaryContainerDark", Color(0xFFA7F3D0), onPrimaryContainerDark)
    }

    @Test
    fun `verify secondary colors are configured`() {
        // Test secondary colors (blue telemetry accent)
        assertEquals("secondaryLight should be blue 600", Color(0xFF2563EB), secondaryLight)
        assertEquals("secondaryDark should be blue 400", Color(0xFF60A5FA), secondaryDark)
    }

    @Test
    fun `verify tertiary colors are configured`() {
        // Test tertiary colors (violet accent)
        assertEquals("tertiaryLight should be violet 600", Color(0xFF7C3AED), tertiaryLight)
        assertEquals("tertiaryDark should be violet 400", Color(0xFFA78BFA), tertiaryDark)
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
