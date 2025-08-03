package dev.hossain.devicecatalog.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test to verify navigation destinations are properly configured.
 */
class NavigationDestinationTest {
    @Test
    fun `verify all navigation destinations are defined`() {
        val destinations = NavigationDestination.destinations

        assertEquals("Should have 4 navigation destinations", 4, destinations.size)

        // Verify each destination exists
        assertTrue(
            "Home destination should exist",
            destinations.any { it is NavigationDestination.Stats },
        )
        assertTrue(
            "Devices destination should exist",
            destinations.any { it is NavigationDestination.Devices },
        )
        assertTrue(
            "Search destination should exist",
            destinations.any { it is NavigationDestination.Search },
        )
        assertTrue(
            "About destination should exist",
            destinations.any { it is NavigationDestination.About },
        )
    }

    @Test
    fun `verify destination properties are correctly set`() {
        val home = NavigationDestination.Stats
        assertEquals("Home route should be 'home'", "home", home.route)
        assertEquals("Home title should be 'Home'", "Home", home.title)

        val devices = NavigationDestination.Devices
        assertEquals("Devices route should be 'devices'", "devices", devices.route)
        assertEquals("Devices title should be 'Devices'", "Devices", devices.title)

        val about = NavigationDestination.About
        assertEquals("About route should be 'about'", "about", about.route)
        assertEquals("About title should be 'About'", "About", about.title)
    }

    @Test
    fun `verify routes are unique`() {
        val destinations = NavigationDestination.destinations
        val routes = destinations.map { it.route }
        val uniqueRoutes = routes.toSet()

        assertEquals("All routes should be unique", routes.size, uniqueRoutes.size)
    }
}
