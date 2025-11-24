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
- FloatingActionButton for quick sharing in device detail view
- Copy-to-clipboard functionality for individual device specifications with snackbar confirmation
- Material 3 TopAppBarScrollBehavior for better scroll experience
- Loading skeleton states with shimmer effect for perceived performance
- Enhanced error state with illustration and improved messaging
- Semantic labels and heading hierarchy for better accessibility
- Minimum 48dp touch targets for all interactive elements
- Enhanced device statistics dashboard with comprehensive analytics
- Swipeable metric cards for mobile-first statistics display
- Animated pie charts for form factor distribution with legends
- Horizontal bar charts for manufacturer, RAM, ABI, and GPU distribution
- Line charts for SDK version adoption metrics
- RAM distribution analysis with percentage breakdowns
- Screen density distribution statistics
- ABI (Application Binary Interface) support metrics
- GPU distribution for top 10 graphics processors
- Collapsible card sections for organized statistics presentation
- Pull-to-refresh functionality in statistics screen
- Percentage calculations for all statistical metrics
- Comprehensive responsive design system with adaptive layouts
- WindowSizeUtils for responsive breakpoint detection (phone <600dp, tablet 600dp+)
- DeviceFormFactor enum for identifying device types (PHONE, TABLET_SMALL, TABLET_LARGE)
- FoldableDeviceUtils for detecting and handling foldable device states
- Support for half-opened horizontal (tabletop mode) and vertical (book mode) fold states
- TwoPaneLayout composable for tablet master-detail layouts
- MasterDetailLayout and AdaptiveListDetailLayout for responsive list+detail views
- AccessibilityUtils with proper touch target size constants (48dp minimum)
- WindowManager dependency for foldable device support
- Adaptive navigation (NavigationRail for tablets, BottomNavigation for phones)
- Multi-column device grid layouts (1 column for phones, 2-3 columns for tablets)
- Responsive content padding and spacing based on device form factor

### Changed
- Replaced paging toggle FAB with filter action FAB
- Updated DevicesScreen state to include search and filter parameters
- Enhanced DevicesPresenter with search and filter logic
- Improved DevicesUi with integrated search bar and filter UI components
- Updated empty state handling to differentiate between no devices and no search results
- Device detail view now uses expandable cards with default states (basic info and specs expanded by default)
- Screen Information and Platform Information sections are collapsed by default to reduce initial scroll
- Error messages are now more user-friendly with better visual hierarchy
- Loading states now show skeleton cards instead of centered spinner
- Enhanced DeviceStats data model to include RAM, SDK, screen density, ABI, and GPU distributions
- Statistics screen now uses Material 3 design with improved visual hierarchy
- App navigation automatically switches between NavigationRail (tablets) and BottomNavigation (phones)
- Device list layout adapts from single column (phones) to multi-column grid (tablets)
- Content padding and spacing adjust based on available screen width

### Fixed
- Ensured Material 3 theme compatibility throughout search and filter components

[Unreleased]: https://github.com/hossain-khan/device-catalog-app/compare/v1.0.0...HEAD
