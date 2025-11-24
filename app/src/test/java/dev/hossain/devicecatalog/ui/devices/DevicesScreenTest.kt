package dev.hossain.devicecatalog.ui.devices

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for DevicesScreen state and filter functionality.
 * Note: Tests using Android-specific classes like FormFactor are excluded
 * as they require Android instrumentation tests.
 */
class DevicesScreenTest {
    @Test
    fun `FilterState should have no active filters by default`() {
        val filterState = DevicesScreen.FilterState()

        assertFalse("Filter state should have no active filters", filterState.hasActiveFilters())
        assertEquals("Active filter count should be 0", 0, filterState.activeFilterCount())
    }

    @Test
    fun `FilterState should detect active manufacturer filters`() {
        val filterState =
            DevicesScreen.FilterState(
                manufacturers = setOf("Samsung", "Google"),
            )

        assertTrue("Filter state should have active filters", filterState.hasActiveFilters())
        assertEquals("Active filter count should be 1", 1, filterState.activeFilterCount())
    }

    @Test
    fun `FilterState should detect active SDK version filters`() {
        val filterState =
            DevicesScreen.FilterState(
                minSdkVersion = 21,
                maxSdkVersion = 33,
            )

        assertTrue("Filter state should have active filters", filterState.hasActiveFilters())
        assertEquals("Active filter count should be 1", 1, filterState.activeFilterCount())
    }

    @Test
    fun `FilterState should handle SDK version with only min version`() {
        val filterState =
            DevicesScreen.FilterState(
                minSdkVersion = 21,
            )

        assertTrue("Filter state should have active filters", filterState.hasActiveFilters())
        assertEquals("Active filter count should be 1", 1, filterState.activeFilterCount())
    }

    @Test
    fun `FilterState should handle SDK version with only max version`() {
        val filterState =
            DevicesScreen.FilterState(
                maxSdkVersion = 33,
            )

        assertTrue("Filter state should have active filters", filterState.hasActiveFilters())
        assertEquals("Active filter count should be 1", 1, filterState.activeFilterCount())
    }

    @Test
    fun `DevicesScreen State should initialize with correct defaults`() {
        val state =
            DevicesScreen.State(
                eventSink = {},
            )

        assertEquals("Devices list should be empty", 0, state.devices.size)
        assertFalse("Should not be loading by default", state.isLoading)
        assertFalse("Should not be refreshing by default", state.isRefreshing)
        assertFalse("Should not be empty by default", state.isEmpty)
        assertFalse("Should not show no search results by default", state.isNoSearchResults)
        assertTrue("Should use paging by default", state.usePaging)
        assertEquals("Search query should be empty", "", state.searchQuery)
        assertEquals("Search result count should be 0", 0, state.searchResultCount)
        assertFalse("Filter sheet should not be shown", state.showFilterSheet)
        assertFalse("Active filters should be empty", state.activeFilters.hasActiveFilters())
    }

    @Test
    fun `DevicesScreen State should handle search state correctly`() {
        val state =
            DevicesScreen.State(
                searchQuery = "Samsung",
                searchResultCount = 42,
                eventSink = {},
            )

        assertEquals("Search query should be set", "Samsung", state.searchQuery)
        assertEquals("Search result count should be set", 42, state.searchResultCount)
    }

    @Test
    fun `DevicesScreen Events should be properly defined`() {
        // Test that non-Android-dependent events can be created
        val refreshDevices = DevicesScreen.Event.RefreshDevices
        val retryLoading = DevicesScreen.Event.RetryLoading
        val togglePagingMode = DevicesScreen.Event.TogglePagingMode
        val searchQueryChanged = DevicesScreen.Event.OnSearchQueryChanged("test")
        val clearSearch = DevicesScreen.Event.ClearSearch
        val showFilterSheet = DevicesScreen.Event.ShowFilterSheet
        val dismissFilterSheet = DevicesScreen.Event.DismissFilterSheet
        val applyFilters = DevicesScreen.Event.ApplyFilters(DevicesScreen.FilterState())
        val clearFilters = DevicesScreen.Event.ClearFilters

        // Verify events are different instances
        assert(refreshDevices != retryLoading)
        assert(searchQueryChanged != clearSearch)
        assert(showFilterSheet != dismissFilterSheet)
        assert(applyFilters != clearFilters)
    }
}
