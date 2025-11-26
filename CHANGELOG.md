# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- Improved color contrast on stats screen following Material 3 guidelines
  - Replaced hardcoded colors with Material 3 color scheme tokens for better theme support
  - Fixed pie chart colors to use `primary`, `secondary`, `tertiary`, `error`, and `primaryContainer`
  - Fixed bar chart colors to use theme-aware colors (primary, secondary, tertiary, error)
  - Fixed page indicator colors to use `primary` and `onSurfaceVariant` with appropriate alpha
  - Fixed line chart to use `primary` color from theme
  - Fixed text color on bar charts to use `onSurface` for proper contrast
  - All colors now adapt to light/dark themes and dynamic color (Android 12+)

### Changed
- Updated Copilot instructions to reflect multi-module architecture
  - Added comprehensive project structure documentation with all core and feature modules
  - Documented convention plugins usage and configuration
  - Updated file path references to use new module locations (e.g., `core/database/src/main/kotlin/`)
  - Added guidelines for working with multi-module architecture
  - Updated code examples to reflect Circuit Screen/Presenter/UI pattern in feature modules
  - Added Future Considerations section referencing Now in Android best practices from issue #72
  - Updated Resources section with Now in Android learning journey links

### Added
- Enhanced debugging logs for device search and paging flow
  - Added tagged Timber logs in `DevicesPresenter` to track search query changes, debouncing, and state calculations
  - Added tagged Timber logs in `DevicesUi` to track UI state transitions and paging behavior
  - Log tags: `DevicesPresenter:Search`, `DevicesPresenter:Query`, `DevicesPresenter:Filter`, `DevicesPresenter:Paging`, `DevicesPresenter:State`, `DevicesUi:Display`, `DevicesUi:Paging`
  - Helps diagnose search and filter issues in development
- Multi-module architecture following Now in Android patterns
  - Core modules: `:core:common`, `:core:data`, `:core:database`, `:core:designsystem`, `:core:model`, `:core:ui`
  - Feature modules: `:feature:devices`, `:feature:devicedetails`, `:feature:statistics`, `:feature:settings`
- Compose Compiler metrics and reports configuration for performance analysis
- Documentation in README for interpreting Compose metrics and reports
- Build-logic convention plugins for consistent build configuration
  - `devicecatalog.android.application` - Configure Android application modules
  - `devicecatalog.android.library` - Configure Android library modules
  - `devicecatalog.android.compose` - Configure Jetpack Compose
  - `devicecatalog.android.feature` - Configure feature modules (compose + metro)
  - `devicecatalog.jvm.library` - Configure pure Kotlin/JVM modules
- Design system components in `:core:designsystem` module
  - `DeviceCatalogIcons` object for centralized icon management with all app icons
  - `DeviceCatalogButton` and variants (Secondary, Outlined, Text) following Material 3 guidelines
  - `DeviceCatalogCard` and `DeviceCatalogElevatedCard` for consistent card designs
  - `DeviceCatalogTopAppBar` for consistent top bar implementation
  - `DeviceCatalogLoadingWheel` and variants for loading states
  - `DeviceCatalogBackground` and gradient background components
  - Preview annotations for all design system components

### Changed
- Refactored to multi-module architecture for improved build times and separation of concerns
- Moved database layer to `:core:database` module
- Moved repository and data classes to `:core:data` module
- Moved theme and design components to `:core:designsystem` module
- Moved domain models to `:core:model` module
- Moved common utilities to `:core:common` module
- Optimized Gradle build performance with advanced JVM and GC settings from Now in Android project
- Enabled parallel execution for faster builds
- Enabled configuration caching for improved build times
- Disabled unused build features (resvalues, shaders) to reduce build overhead
- Refactored app module to use convention plugins, removing duplicated build configuration
- Updated all app module files to use theme from `:core:designsystem` instead of local `ui.theme` package
- Migrated theme tests from app module to `:core:designsystem` module

### Fixed
- Removed redundant Scaffold from bottom navigation layout to prevent double-scaffold issues
  - Screens already provide their own Scaffold with TopAppBar
  - Navigation layer now only provides navigation chrome (bottom bar, rail, drawer)
  - Improves layout consistency and prevents padding/inset conflicts
- Fixed search results not displaying in device list when using paging mode
  - Issue: `isNoSearchResults` was checking `filteredDevices` which is only used in non-paging mode
  - When paging is enabled, results come from `pagedDevices` flow, making `filteredDevices` always empty
  - Solution: Disabled `isNoSearchResults` check when using paging mode
  - Added empty state handling to `PaginatedDeviceList` using paging library's LoadState
  - Search results now display correctly in both paging and non-paging modes

### Removed
- Duplicate theme files from app module (`ui.theme` package)
- Old theme test from app module (migrated to `:core:designsystem`)

## [1.0.0] - 2025-11-25

### Added
- DeviceCatalogNavigationType enum for adaptive navigation (Bottom Navigation, Navigation Rail, Permanent Drawer) following Reply app best practices
- DeviceCatalogContentType enum for determining single pane vs dual pane layouts
- Permanent navigation drawer support for expanded screens (840dp+) with app title and navigation items
- WindowSizeClass extension functions `toNavigationType()` and `toContentType()` for adaptive layout decisions
- Multi-device preview annotations for navigation components (Compact, Medium, Expanded)
- Unit tests for DeviceCatalogNavigationType and DeviceCatalogContentType utilities
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
- Multi-column device grid layouts (1 column for phones, 2-3 columns for tablet devices)
- Responsive content padding and spacing based on device form factor
- Database indexes on frequently queried columns for 30-50% faster queries (manufacturer, model_name, brand, form_factor)
- Coil image loading library for efficient memory and disk caching
- @Immutable annotations on data classes for Compose recomposition optimization
- Performance monitoring utilities for tracking startup time and memory usage
- Battery-efficient DeviceSyncWorker with smart constraints (charging, WiFi, battery not low)
- Startup time tracking with automatic logging and warning thresholds
- Memory usage monitoring with automatic logging
- App shortcuts for quick access to Search, Statistics, and Bookmarks (Android 7.1+)
- Notification channels for device sync updates (Android 8.0+)
- Splash screen API implementation with Material 3 design (Android 12+)
- Haptic feedback utility with Material Design patterns (click, long press, reject, gesture feedback)
- Per-app language preferences support with 7 languages (Android 13+ with legacy support)
- Network security configuration with HTTPS enforcement and localhost debugging support
- Secure storage using EncryptedSharedPreferences for sensitive data (AES256 GCM encryption)
- Feature flags system for gradual rollouts and A/B testing
- Developer settings screen showing feature flags and performance metrics
- Deep linking infrastructure for device details (HTTPS and custom scheme support)

### Changed
- AppNavigation now uses DeviceCatalogNavigationType for cleaner navigation type selection following Reply app patterns
- Navigation adapts between three modes: Bottom Navigation (phones), Navigation Rail (small tablets), and Permanent Drawer (large tablets/desktops)
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
- Database version upgraded to 2 with auto-migration for index addition
- Optimized pagination configuration (page size 30, max 150 items, prefetch 15) for better memory efficiency
- Enhanced DAO queries with multi-column sorting and brand search support
- DevicesPresenter now uses remember() for all computed values to reduce recompositions
- Application class now tracks app startup time and memory usage on launch
- MainActivity records first frame time for performance analysis

### Fixed
- Ensured Material 3 theme compatibility throughout search and filter components
- Fixed lint errors by replacing deprecated `Modifier.composed` with standard modifier chains
- Fixed Compose parameter ordering errors by making `modifier` the first optional parameter
- Fixed unremembered state objects by wrapping `derivedStateOf` with `remember`
- Fixed restricted API usage by avoiding direct `WindowLayoutInfo` constructor call

[Unreleased]: https://github.com/hossain-khan/device-catalog-app/compare/1.0.0...HEAD
[1.0.0]: https://github.com/hossain-khan/device-catalog-app/releases/tag/1.0.0
