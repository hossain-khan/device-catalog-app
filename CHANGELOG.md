# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.7.0] - 2026-11-19

### Added
- Added a **Compare** action in `DeviceDetailsUi` top app bar that opens `DeviceComparisonScreen` with the current device pre-selected (#285)
- Added tappable full-screen lightbox dialog (`DeviceImageLightboxDialog`) with pinch-to-zoom, pan, and double-tap zoom on loaded device images in `DeviceDetailsUi` (#284)
- Added `:benchmarks` module with `devicecatalog.android.test` convention plugin, `androidx.baselineprofile` Gradle plugin, `androidx.profileinstaller` in `:app`, Baseline Profile generators (`StartupBaselineProfile`, `DeviceCatalogBaselineProfile`), Macrobenchmarks (`StartupBenchmark`, `ScrollDevicesListBenchmark`), and generated release/startup profiles (#275, #282, #283)
- Added Roborazzi screenshot testing module (`:core:screenshot-testing`) and baseline screenshot tests across key screens (#281)
- Added custom `:lint` module and `AndroidLintConventionPlugin` enforcing design-system usage and test naming conventions (#280)

### Changed
- Added `compose_compiler_config.conf` for Compose stability of core, parser, and immutable collection models (#276)
- Enabled Gradle Isolated Projects, strict Configuration Cache, and re-enabled `:core:testing` unit tests with expanded presenter/repository test coverage (#277, #278, #279)

## [2.6.0] - 2026-11-18

### Changed
- Replaced oversized `MediumTopAppBar` / `LargeTopAppBar` across all four bottom-navigation tabs (`Devices`, `Quiz Hub`, `Stats`, `About`) with a compact two-line `DeviceCatalogTabTopBar` featuring a tinted icon badge, live subtitle, and contextual actions (#272)
- Added live device count (`23,997 devices`), snapshot date (`Nov 11, 2026`), parser version (`Parser v1.13`) pills, and an external link to `android-device-catalog-parser` on the About screen's **Device Catalog Database** card (#269, #271)

### Fixed
- Fixed `QuizHub` tile subtitles clipping on narrower screens/larger font scales by replacing `aspectRatio(0.88f)` with `defaultMinSize` and equal-height `IntrinsicSize.Max` rows (#270)
- Fixed double loading indicator on initial load of the Statistics screen by showing the `PullToRefreshBox` spinner only when refreshing already-loaded stats (#273)

## [2.5.0] - 2026-11-18

### Changed
- Migrated to `android-device-catalog-parser` `1.13` and Room database v5 with precomputed `sort_score`, `min_sdk`, `max_sdk`, `ram_min_mb`, and `ram_max_mb` columns and covering index for instant flagship-first device listing (#264, #267)
- Added `device_fts` FTS4 virtual table and multi-token prefix search in `AndroidDeviceDao` / `AndroidDeviceRepository` (#267)
- Defaulted all sections on the Statistics screen to collapsed and retained expand/collapse state across scrolling via `rememberRetained` (#265)

### Fixed
- Fixed Stats Explorer briefly showing the previous tab's data when switching categories by resetting `produceRetainedState` and caching loaded stats per category (#266)

## [2.4.0] - 2026-11-17

### Changed
- Updated bundled device catalog database to v4 with the latest Google Play device catalog snapshot (23,997 devices) (#260)
- Upgraded `android-device-catalog-parser` from `1.8` to `1.12` and added UI support for `FormFactor.DESKTOP` and `FormFactor.UNKNOWN` (#260)
- Updated `DATABASE_UPDATE_GUIDE.md` to use `android-device-catalog-parser` GitHub Release assets and short-circuited CI builds for docs-only changes (#261, #262)

## [2.3.0] - 2026-11-16

### Added
- Implemented and enabled **Spec Master** (`:feature:specmaster`) quiz in `QuizHub` ("Guess the specs" for Processor/SoC, RAM, and GPU backed by real device catalog fields)
- Enabled **Device Compare** (`:feature:devicecomparison`) in `QuizHub` with aligned 1:1 device header and spec columns, top-bar add action, improved device selector bottom sheet, fixed RAM range comparison (`11-12GB`), and `DeviceComparisonPresenterTest`

### Fixed
- Avoided adding unchanged default RAM/DPI/SDK slider ranges as active filter chips in `FilterBottomSheet`
- Deduplicated repeated manufacturer prefixes in `SpecMasterService` device names (e.g., `Blaupunkt` instead of `Blaupunkt Blaupunkt`)
- Added vertical scrolling to `QuizUi` so options and the `Submit` button remain accessible on compact landscape screens
- Fixed `BrandChallengeService` question generation so `"Which brand does NOT belong to [manufacturer]?"` includes exactly one non-belonging brand instead of two, and multi-manufacturer brands are excluded from wrong options in `MANUFACTURER_FOR_BRAND` and `TRUE_FALSE` questions
- Fixed `QuizService` question generation to deduplicate device codenames and model names so answer choices never contain duplicate model names

### Removed
- Removed `Timeline Challenge` (`TIMELINE_CHALLENGE`) placeholder from `QuizHub` because the Play Console dataset has no release dates/years
- Removed Dream Phone Finder (`:feature:dreamphone`) and its entry points in `QuizHub` and `DevicesScreen` because the Play Console dataset lacks pricing, camera, battery, and display panel/refresh-rate specs needed for reliable recommendations

## [2.2.1] - 2026-11-16

### Fixed
- Fixed `Failed to load devices: The coroutine scope left the composition` snackbar shown when returning from `DeviceDetailsScreen` by rethrowing `CancellationException` in `produceRetainedState` and `LaunchedEffect` coroutine catch blocks across presenters (#247, #248)

## [2.2.0] - 2026-11-15

### Fixed
- Fixed phone quiz back navigation so pressing Back (top bar or system gesture) from `QuizResultsScreen` returns directly to `QuizHubScreen` instead of returning to completed quiz questions, and prevented `PlayAgain` / `ChooseManufacturer` from stacking duplicate screens (#244)

### Changed
- Updated Gradle (`9.6.0`), Android Gradle Plugin (`9.5.1`), Kotlin (`2.4.21`), KSP (`2.3.13`), AndroidX Paging (`3.5.3`), DataStore (`1.3.0`), Room (`2.8.5`), and `kotlinx.serialization` (`1.13.0`) (#245)
- Tidied `README.md` and updated `.github/copilot-instructions.md` for Circuit `0.39.0` and Metro `1.5.2` conventions (#242, #243)
- Upgraded Metro DI from `1.4.1` to `1.5.2` and adopted official `metrox-android` (`MetroAppComponentFactory`, `MetroAppComponentProviders`, `MetroApplication`, implicit `@ActivityKey`) in place of custom `ComposeAppComponentFactory`, `ActivityKey`, and `UiMultibindings`
- Replaced Circuit's KSP code generator (`circuit-codegen`) with Metro's built-in compiler-plugin Circuit codegen (`enableCircuitCodegen.set(true)`) across `:app` and all feature modules via `AndroidFeatureConventionPlugin`, removing KSP from `:feature:*`, `:core:common`, and `:core:data`
- Enabled automatic Metro hidden-dependency validation (`hiddenDependencies.checkOnCompile.set(true)`) and added missing direct `:feature:devicecomparison` dependency to `:app`
- Converted `CircuitProviders` and `AppWorkerFactory.Bindings` to `@ContributesTo(AppScope::class) @BindingContainer` with `@Multibinds(allowEmpty = true)`, converted graph and presenter `@AssistedFactory` declarations to `fun interface Factory`, cleaned up redundant `AppGraph` annotations/container declarations and unused `DataMultibindings` / `javax.inject` leftovers
- Upgraded Slack Circuit from `0.36.1` to `0.39.0` and added `circuit-serialization` and `circuit-test`
- Migrated all 14 `Screen` definitions from `@Parcelize`/`Parcelable` to `@CircuitSerializable(AppScope::class)` with `SerializableCircuitSaver` multibound via `CircuitProviders`
- Migrated `AppNavigation` from `rememberSaveableBackStack` + `goTo()` tab switching to `rememberSaveableNavStack` + `Navigator.StateOptions.SaveAndRestore` (`resetRoot`), `rememberAndroidScreenAwareNavigator`, and `NavStack.rootRecord`-derived tab selection
- Replaced direct `Context.startActivity` calls in `AboutPresenter` and `DeviceDetailsPresenter` with Circuit's `IntentScreen`
- Updated presenters (`DevicesPresenter`, `DeviceDetailsPresenter`, `DeviceStatsPresenter`, `StatsExplorerPresenter`, `DeviceComparisonPresenter`, `DreamPhonePresenter`, `OnboardingPresenter`, `DeveloperSettingsPresenter`, `BrandChallengePresenter`, `QuizPresenter`, `ManufacturerSelectionPresenter`) to use `produceRetainedState` and `rememberRetained` for values instead of retaining raw `Flow`/`CoroutineScope` instances or losing state across configuration changes
- Enabled `unitTests.isReturnDefaultValues = true` in `KotlinAndroid` convention plugin

### Added
- Added Circuit presenter unit tests (`QuizHubPresenterTest`, `QuizPresenterTest`, `QuizResultsPresenterTest`, `DeviceDetailsPresenterTest`, `DevicesPresenterTest`) using `circuit-test` (`Presenter.test`, `FakeNavigator`) (#239, #244)

## [2.1.0] - 2026-11-15

### Fixed
- Fixed vertical misalignment between form factor and codename badges in `DeviceDetailsUi` hero card (#236)
- Fixed vertical alignment between search input field and `Filters` button in `DeviceSearchBar` (#236)
- Fixed `DevicesPresenter` and `DevicesUi` losing active filters, search query, and scroll state (or reloading all devices with skeleton placeholders) when navigating back from `DeviceDetailsScreen` (#236)
- Moved `"23,000+ devices"` badge to its own row below `"Device Catalog Database"` in `AboutUi` so title and description have full horizontal width (#236)

## [2.0.0] - 2026-11-15

### Changed
- Executed comprehensive "Developer Precision Console" UI/UX overhaul across all modules (#233, #234):
  - **Core Design System (`:core:designsystem`)**: Replaced olive-green palette with Alabaster Slate (light) and Obsidian Carbon (dark) accented by Android Emerald; added `Plus Jakarta Sans`, `Inter`, and `JetBrains Mono` typography with tabular numerals (`tnum`); updated `DeviceCatalogCard` and `DeviceTypeColors` with `1.dp` hairline borders and semantic accent tokens
  - **Device Browser (`:feature:devices`)**: Redesigned `DeviceCard` with top semantic form-factor accent bar, uppercase manufacturer eyebrow, monospace codename badge, and 4-pill hardware telemetry strip (`RAM`, `API`, `DPI`, `SoC/ABI`); replaced floating filter FAB with an integrated Developer Command Bar (`DeviceSearchBar` + filter badge button)
  - **Device Details (`:feature:devicedetails`)**: Redesigned `DeviceHeroCard` onto a neutral bordered surface, added a 2×2 `KeySpecsBentoGrid` (`MEMORY`, `TARGET SDK`, `ARCHITECTURE`, `DISPLAY DENSITY`), made all spec sections expanded by default, and styled technical chips in monospace
  - **Statistics & Stats Explorer (`:feature:statistics`, `:feature:statsexplorer`)**: Replaced full-bleed swipeable metric pager with a high-density 2×2 `StatsKpiGrid` bento layout and upgraded horizontal distribution charts with rank badges (`#1`, `#2`), tabular counts, percentage pills, and `6.dp` progress tracks
  - **Quizzes, Comparison, About & Navigation (`:feature:devicecomparison`, `:feature:quizhub`, `:feature:aboutapp`, `:app`)**: Refined comparison matrix cells, `QuizTile` cards, `AboutUi` console identity card, and `AppNavigation` bars/rails with `1.dp` `outlineVariant` borders

## [1.13.0] - 2026-11-12

### Added
- Comprehensive Material 3 design and edge-to-edge modernization across all app screens and flows (#223, #228)
- Interactive Material 3 full-screen search experience with popular suggestion chips (`DeviceSearchBar`) (#225)
- Centralized Material 3 icon definitions in `:core:designsystem` (`DeviceCatalogIcons`) (#229, #230)
- Animated Material 3 pill page indicator in `OnboardingUi` with smooth size and color transitions (#231)

### Changed
- Converted primary top-level destinations to editorial collapsing app bars (`LargeTopAppBar` and `MediumTopAppBar`) with responsive scroll behaviors (#225, #230):
  - `DevicesUi`: upgraded to `MediumTopAppBar` with `enterAlwaysScrollBehavior()`
  - `QuizHubUi`, `DeviceStatsUi`, and `AboutUi`: upgraded to `LargeTopAppBar` with `exitUntilCollapsedScrollBehavior()`
- Modernized filter bottom sheet (`FilterBottomSheet.kt`) (#224):
  - Pinned persistent bottom action bar ("Clear All", "Apply Filters") docked above system navigation insets
  - Added leading checkmark icons on selected `FilterChip`s
  - Migrated RAM, DPI, and SDK range sliders to modern `RangeSliderState`
- Standardized surface container hierarchy and color role pairings with WCAG-compliant contrast (#226):
  - Replaced ad-hoc surface alpha blends with canonical `surfaceContainerLow` and `surfaceContainerLowest` tonal containers
  - Calibrated form factor badge colors with dedicated accessible `onColor` contrast pairings
- Upgraded Stats Explorer controls and expressive metric cards (#227):
  - Replaced legacy filter chip row with Material 3 `SingleChoiceSegmentedButtonRow`
  - Replaced custom filter dropdown with horizontal scrollable filter chips
  - Redesigned metric summary cards with prominent hero values and category badge pills
- Modernized `AboutUi` and `DeveloperSettingsUi` (#230):
  - Grouped About screen links and catalog database details into structured `surfaceContainerLow` cards
  - Consolidated Developer Settings feature flags into a single card container with dividers and M3 `thumbContent` checkmark switches
- Enhanced progress indicators and selection animations (#231):
  - Upgraded `LinearProgressIndicator` to 8dp thickness, `StrokeCap.Round`, and `surfaceContainerHighest` track color in `QuizUi`, `BrandChallengeUi`, and `DreamPhoneUi`
  - Added smooth animated color transitions (`animateColorAsState`) and animated correctness feedback icons to quiz answer option cards

### Fixed
- Fixed edge-to-edge window inset conflicts in child screens (`QuizUi`, `BrandChallengeUi`, `ManufacturerSelectionUi`, `StatsExplorerUi`, `DeviceComparisonUi`) by respecting system navigation bar insets, ensuring action buttons never collide with the system gesture bar pill (#228, #229)
- Fixed bottom navigation bar icon for Stats tab by updating from generic `Star` to semantic `QueryStats` icon (#228, #229)
- Fixed potential content clipping on `AboutUi` by making layout vertically scrollable for smaller displays and high font scaling (#228, #230)

## [1.12.0] - 2026-11-12

### Added
- Comprehensive UI/UX refinements validated on Google Pixel 10 Pro XL display (#220, #221)

### Changed
- Refactored Jetpack Compose component design across `:core:designsystem` and feature modules adhering to official API guidelines (#219):
  - Standardized root layout modifier placement and chained modifier conventions
  - Converted boolean shape/style flags to semantic role/variant APIs
  - Promoted variable content to `@Composable () -> Unit` slot parameters
- Optimized Jetpack Compose motion and animations for lifecycle correctness and performance (#218):
  - Standardized enter/exit visibility transitions using `AnimatedVisibility`
  - Migrated state transitions to `rememberTransition` / `updateTransition`
  - Replaced ad-hoc conditional layout branching with `Crossfade`
- Reorganized `DeviceDetailsUi` header cards into a single unified `DeviceHeroCard` with clear `Codename` identification and TopAppBar share action (#221)
- Improved `QuizResultsUi` layout by placing "Play Again" and "Choose Manufacturer" actions directly below the score card (#221)
- Form factor filter chips now display formatted title-case text instead of raw enum names (#221)

### Fixed
- Fixed bottom navigation bar leaking onto non-top-level destinations (Onboarding, Quiz, Device Details, Settings, Dream Phone) (#221)
- Fixed "Skip" button collision with status bar cutout and clock on tall displays in `OnboardingUi` (#220, #221)
- Fixed RAM formatting edge cases where identical start/end gigabytes produced redundant ranges (e.g., `1GB` instead of `1-1GB`) (#221)
- Fixed text clipping on Quiz Hub cards on high-DPI devices by adjusting aspect ratio and icon sizing (#220, #221)
- Fixed single-letter word wrapping (`Distributi\non`, `Processor\ns`) on Stats Explorer cards by increasing width to 140dp (#220, #221)

## [1.11.0] - 2026-11-12

### Added
- Device image loading support using Coil 3 with CDN hosting and fallback candidate URLs (#215)
- Reusable `DeviceImage` composable in `:core:ui` with smooth crossfade and form-factor icon fallback
- Device image preview cards in device details and device comparison selector
- `AGENTS.md` guidelines for AI coding agents (#216)

### Changed
- Upgraded dependencies (#216):
  - Coil: 3.5.0 → 3.6.3
  - Compose BOM: 2026.08.00 → 2026.09.00
  - Material 3: 1.5.0-alpha26 → 1.5.0-rc01
  - AndroidX WorkManager: 2.11.2 → 2.12.0
  - Compose Lint Checks: 1.5.4 → 1.6.1
  - Core KTX: 1.19.0 → 1.19.1
  - Google Fonts: 1.12.0 → 1.12.1
  - Kotlinx Serialization: 1.11.0 → 1.12.0
- Enhanced Material You theme with comprehensive color schemes
  - Replaced simple blue-based colors with full Material 3 color system
  - Added complete light/dark theme with all Material 3 color tokens (56+ colors per theme)
  - Changed seed color from blue to green for tech/nature balance aesthetic
  - Includes surface variants, containers, and inverse colors for better Material You support
  - **Disabled dynamic color** to ensure consistent vibrant chart colors across all devices
  - Improved color harmony and contrast across all UI components
- Upgraded Stats Explorer charts with vibrant theme-aware colors
  - Added 8 vibrant chart colors optimized for data visualization
  - Separate color palettes for light and dark themes with excellent contrast
  - Chart colors designed for clear differentiation and readability
  - All charts (bar, donut, line, stacked) now use consistent vibrant colors
  - Improved text contrast on chart elements and legends
  - Theme-aware color selection ensures visibility in both light and dark modes

### Fixed
- Fixed search filtering not displaying results in paging mode
  - Issue: Search showed result count in title (e.g., "Search Result (43)") but displayed "no results" message
  - Root cause: `isNoSearchResults` was checking `filteredDevices` which is only populated in non-paging mode
  - Solution: Only check `isNoSearchResults` when `usePaging=false`, as paging mode uses `pagedDevices` flow
  - Added comprehensive Timber logging throughout search/filter flow for debugging
- Migrated DeviceSearchBar from deprecated SearchBar API to new inputField-based API
  - Updated to use `SearchBar` with `inputField` parameter instead of deprecated constructor
  - Uses `SearchBarDefaults.InputField` for proper Material 3 search bar implementation
  - Maintains all existing functionality: search query, clear button, icons, semantics
  - Removes deprecation warning: "Use overload which takes inputField as a parameter"

## [1.2.0] - 2025-12-07

### Added
- Metro DI multi-module support with `:core:di` module
  - Created `:core:di` module for centralized dependency injection infrastructure
  - Added `ActivityKey` MapKey annotation for type-safe Activity injection across modules
  - Added `WorkerKey` MapKey annotation for WorkManager integration with Metro DI
  - Added `UiMultibindings` interface using `@ContributesTo` pattern for automatic discovery
  - Updated all module imports to use `core:di` for DI infrastructure
  - Added comprehensive Metro DI best practices documentation in Copilot instructions
  - Documented multi-module DI patterns following [CatchUp](https://github.com/ZacSweers/CatchUp) best practices
  - Added module README with usage examples and references
- Device Statistics Explorer feature (`:feature:statsexplorer`)
  - Interactive data visualization for device statistics with multiple chart types
  - Six stat categories: RAM Distribution, Processors, Form Factors, Manufacturers, SDK Versions, OpenGL Versions
  - Animated charts: horizontal bar charts, donut charts, line/area charts, stacked bar charts
  - Auto-generated insights and trends for each category
  - Category selector with swipeable cards showing item counts
  - Distribution details with percentage breakdowns
  - Pull-to-refresh support for data updates
  - Repository methods: `getProcessorDistribution()`, `getOpenGlDistribution()`
  - Data classes: `ProcessorCount`, `OpenGlCount` for distribution statistics
  - Circuit Screen/Presenter/UI pattern with Metro dependency injection
  - Material 3 UI with theme-aware colors and animations
  - Navigate to Stats Explorer from Device Statistics screen via Explore icon
- GitHub Codespaces dev container support for Android development
  - Added `.devcontainer/devcontainer.json` with Java 21 base image and Android SDK environment
  - Added `.devcontainer/post-create.sh` script for automatic Android SDK setup
  - Added `.devcontainer/README.md` documentation for dev container usage
  - Pre-installed VS Code extensions: Kotlin, Gradle, Java, GitHub Copilot, IntelliJ IDEA Keybindings
  - Configured Android SDK Platform 36 and Build Tools 36.0.0 matching project requirements
- Device Comparison Tool feature (`:feature:devicecomparison`)
  - Interactive tool to compare 2-4 Android devices side by side
  - Device selection via search with bottom sheet UI
  - Side-by-side comparison table with grouped specifications (Basic Info, Hardware, Display, Platform)
  - Visual highlighting of best values (highest RAM, screen density, SDK version) with star indicators
  - Color-coded cells: `primaryContainer` for highlighted best values, `surfaceVariant` for regular values
  - SelectedDeviceCard and AddDeviceCard components for device carousel
  - ComparisonTable, SpecRow, and DeviceSelector components for comparison display
  - Navigation to device details from comparison cards
  - Circuit Screen/Presenter/UI pattern with Metro dependency injection
  - Added Compare icon to DeviceCatalogIcons in design system
  - Added to Quiz Hub as available tool tile
- APK Size Analysis workflow using Diffuse for pull requests
  - Automatic comparison of APK sizes between PR builds and latest release
  - PR comments with detailed analysis of size changes (DEX, resources, assets)
  - Artifact upload for manual APK inspection
  - Uses [Diffuse](https://github.com/JakeWharton/diffuse) via [diffuse-action](https://github.com/usefulness/diffuse-action)
- Brand Challenge Quiz feature (`:feature:brandchallenge`)
  - Interactive quiz game testing knowledge of brand ownership and manufacturer relationships
  - Three quiz question types: "Which manufacturer makes [brand]?", "Is [brand] made by [manufacturer]?" (True/False), "Which brand does NOT belong to [manufacturer]?"
  - 5 mixed questions with randomized types for variety
  - Material 3 UI with color-coded answer feedback (correct/incorrect indicators)
  - Quiz completion screen with score, accuracy percentage, and motivational messages
  - Repository methods: `getDistinctBrandManufacturerPairs()`, `getBrandsByManufacturer()`, `getManufacturerForBrand()`
  - BrandChallengeService for question generation, scoring, and accuracy calculation
  - Circuit Screen/Presenter/UI pattern with Metro dependency injection
  - Added to Quiz Hub as available quiz type
  - Retry functionality to generate new quiz questions
- Phone Codename Quiz feature (`:feature:phonequiz`)
  - Interactive quiz game to test Android device knowledge by matching device codenames to model names
  - Manufacturer selection screen: grid of manufacturers with device counts, requiring minimum 5 devices
  - Quiz screen: 5 multiple-choice questions with device codenames, 4 answer options per question
  - Results screen: score display, accuracy percentage, motivational messages, review of all answers
  - Quiz logic: question generation with randomized options, answer shuffling, scoring system
  - Navigation flow: Quiz Hub → Manufacturer Selection → Quiz → Results
  - Components: ManufacturerCard, QuestionCard, AnswerOption, AnswerReviewCard, ScoreCard
  - Repository methods: `getManufacturersWithMinDevices()`, `getDevicesByManufacturer()`
  - QuizService for question generation, scoring, and accuracy calculation
  - Material 3 theming with proper color schemes for light/dark modes
  - Visual feedback for answer correctness (checkmark/cross icons, color indicators)
  - Circuit Screen/Presenter/UI pattern with Metro dependency injection
  - Device details integration from results screen for learning more about devices
- Quiz Hub navigation item and screen
  - Added "Quiz" navigation item positioned between "Devices" and "Statistics" with Psychology icon
  - Created `:feature:quizhub` module following feature module architecture
  - Implemented Quiz Hub screen using Circuit pattern (Screen/Presenter/UI)
  - Added grid layout displaying quiz types as interactive tiles
  - Implemented four quiz types: Codename Guess (available), Dream Phone Finder, Spec Master, Timeline Challenge (coming soon)
  - Added "Coming Soon" badge for unavailable quizzes
  - Added Material 3 elevated cards with proper theming for light/dark modes
  - Added Quiz icon to DeviceCatalogIcons in design system
- Dream phone survey feature (`:feature:dreamphone`) inspired by Jetsurvey Compose sample
  - 8-question guided wizard to help users discover their ideal Android device
  - Survey questions cover: primary use case, budget range, form factor, performance priority, camera importance, battery life, screen preferences, and brand preferences
  - Intelligent matching algorithm that scores devices based on user preferences (0-100 scale)
  - Material 3 components throughout: `Card`, `RadioButton`, `Checkbox`, `Slider`, `LinearProgressIndicator`
  - Results screen showing top 10 matching devices with match percentage and reasons
  - Added Psychology icon to `DeviceCatalogIcons` for dream phone feature
  - Icon button in device list top app bar to access the survey wizard
  - Circuit Screen/Presenter/UI pattern with Metro dependency injection
  - Smooth animations between questions and results using `AnimatedContent`
  - Progress indicator showing current question and completion percentage
  - Navigation integration with device details screen from results

### Changed
- Dream Phone Finder is now accessible from Quiz Hub
  - Updated `isAvailable` flag for Dream Phone Finder quiz type from `false` to `true`
  - Added navigation handler in QuizHubPresenter to navigate to DreamPhoneScreen
  - Users can now tap the "Dream Phone Finder" tile in Quiz Hub to start the survey

## [1.1.0] - 2025-11-26

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
- Navigation to Developer Settings screen from About screen (debug builds only)
  - Added Circuit Screen/Presenter/UI pattern to DeveloperSettingsScreen
  - Added Developer Settings button in About screen with bug report icon
  - Button is only visible in debug builds using BuildConfig.DEBUG
  - On release builds, the button is hidden completely

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
- Updated Copilot instructions to reflect multi-module architecture
  - Added comprehensive project structure documentation with all core and feature modules
  - Documented convention plugins usage and configuration
  - Updated file path references to use new module locations (e.g., `core/database/src/main/kotlin/`)
  - Added guidelines for working with multi-module architecture
  - Updated code examples to reflect Circuit Screen/Presenter/UI pattern in feature modules
  - Added Future Considerations section referencing Now in Android best practices from issue #72
  - Updated Resources section with Now in Android learning journey links

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
- Fixed lint errors in feature:devices module
  - Wrapped `derivedStateOf` calls with `remember` in `DevicesPresenter.kt` to prevent state recreation on recomposition
  - Suppressed `ConfigurationScreenWidthHeight` lint warning in `DeviceListLayoutConfig.kt` with justification (LocalWindowInfo not available in current Compose version)
- Improved color contrast on stats screen following Material 3 guidelines
  - Replaced hardcoded colors with Material 3 color scheme tokens for better theme support
  - Fixed pie chart colors to use `primary`, `secondary`, `tertiary`, `error`, and `primaryContainer`
  - Fixed bar chart colors to use theme-aware colors (primary, secondary, tertiary, error)
  - Fixed page indicator colors to use `primary` and `onSurfaceVariant` with appropriate alpha
  - Fixed line chart to use `primary` color from theme
  - Fixed text color on bar charts to use `onSurface` for proper contrast
  - All colors now adapt to light/dark themes and dynamic color (Android 12+)

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

[Unreleased]: https://github.com/hossain-khan/device-catalog-app/compare/2.7.0...HEAD
[2.7.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.6.0...2.7.0
[2.6.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.5.0...2.6.0
[2.5.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.4.0...2.5.0
[2.4.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.3.0...2.4.0
[2.3.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.2.1...2.3.0
[2.2.1]: https://github.com/hossain-khan/device-catalog-app/compare/2.2.0...2.2.1
[2.2.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/hossain-khan/device-catalog-app/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.13.0...2.0.0
[1.13.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.12.0...1.13.0
[1.12.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.11.0...1.12.0
[1.11.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.10.0...1.11.0
[1.2.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/hossain-khan/device-catalog-app/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/hossain-khan/device-catalog-app/releases/tag/1.0.0
