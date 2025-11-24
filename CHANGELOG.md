# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Search functionality with real-time search and 300ms debouncing for device name, manufacturer, and brand
- Filter system with form factor, manufacturer, and SDK version range filtering
- Material 3 SearchBar component with clear functionality
- Filter bottom sheet with Material 3 styling for selecting filters
- Active filter chips display showing applied filters with individual removal capability
- Pull-to-refresh functionality using Material 3 PullToRefreshBox
- Filter FAB with badge showing count of active filters (replaces paging toggle FAB)
- Separate empty states for "no devices" vs "no search results" scenarios
- Search result count display in app bar title
- Error handling with Snackbar and retry action
- Debounced search implementation for optimal performance
- Sharing functionality for device specifications via Android sharing system
- Expandable/collapsible specification sections in device detail view with smooth animations
- Share button in device detail TopAppBar for easy access
- Emoji-enhanced shareable device text format

### Changed
- Replaced paging toggle FAB with filter action FAB
- Updated DevicesScreen state to include search and filter parameters
- Enhanced DevicesPresenter with search and filter logic
- Improved DevicesUi with integrated search bar and filter UI components
- Updated empty state handling to differentiate between no devices and no search results
- Device detail view now uses expandable cards with default states (basic info and specs expanded by default)
- Screen Information and Platform Information sections are collapsed by default to reduce initial scroll

### Fixed
- Ensured Material 3 theme compatibility throughout search and filter components

[Unreleased]: https://github.com/hossain-khan/device-catalog-app/compare/v1.0.0...HEAD
