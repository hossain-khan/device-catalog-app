# Now in Android Best Practices - GitHub Issues

This document contains GitHub issue specifications based on the analysis of [Now in Android](https://github.com/android/nowinandroid) app best practices. Each issue is designed to be actionable by AI agents for incremental improvements to the Device Catalog app.

---

## Issue 1: Implement Multi-Module Architecture with Feature Modules

### Title
Refactor to Multi-Module Architecture with Feature Modules

### Labels
`enhancement`, `architecture`, `breaking-change`

### Priority
High

### Description
Refactor the current single-module architecture to a multi-module architecture following Now in Android patterns. This improves build times, enables parallel development, enforces separation of concerns, and reduces merge conflicts.

### Current State
The app currently has a single `:app` module containing all code including UI, data, domain, and database layers.

### Proposed Changes
Create the following module structure:

```
:app                          # Application module
:core:common                  # Common utilities, dispatchers, Result wrapper
:core:data                    # Repository implementations
:core:database                # Room database, DAOs, entities
:core:designsystem            # Theme, icons, reusable UI components
:core:model                   # Domain models (pure Kotlin)
:core:ui                      # Shared UI components that depend on models
:feature:devices              # Devices list feature
:feature:devicedetails        # Device details feature
:feature:statistics           # Statistics feature
:feature:settings             # Settings feature
```

### Acceptance Criteria
- [ ] Create core modules (common, data, database, designsystem, model, ui)
- [ ] Create feature modules (devices, devicedetails, statistics, settings)
- [ ] Move existing code to appropriate modules
- [ ] Configure inter-module dependencies correctly
- [ ] Feature modules should not depend on each other
- [ ] Core modules should not depend on feature or app modules
- [ ] All existing tests should pass
- [ ] Build completes successfully

### References
- [Now in Android Modularization Journey](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
- [Android Guide to App Architecture](https://developer.android.com/topic/architecture)

---

## Issue 2: Create Build Logic Convention Plugins

### Title
Implement Build Logic Convention Plugins for Consistent Build Configuration

### Labels
`enhancement`, `build`, `developer-experience`

### Priority
Medium

### Description
Create a `build-logic` included build with convention plugins to centralize and standardize build configuration across modules. This eliminates duplicate build script setup and ensures consistent configuration.

### Current State
Build configuration is defined in individual module build files with potential for duplication and inconsistency.

### Proposed Changes
Create the following structure:

```
build-logic/
├── convention/
│   └── src/main/kotlin/
│       ├── AndroidApplicationConventionPlugin.kt
│       ├── AndroidLibraryConventionPlugin.kt
│       ├── AndroidComposeConventionPlugin.kt
│       ├── AndroidFeatureConventionPlugin.kt
│       ├── JvmLibraryConventionPlugin.kt
│       └── KotlinAndroid.kt (shared configuration)
├── settings.gradle.kts
└── gradle.properties
```

### Plugins to Create
1. `devicecatalog.android.application` - Configure Android application modules
2. `devicecatalog.android.library` - Configure Android library modules
3. `devicecatalog.android.compose` - Configure Jetpack Compose
4. `devicecatalog.android.feature` - Configure feature modules (compose + metro)
5. `devicecatalog.jvm.library` - Configure pure Kotlin/JVM modules

### Acceptance Criteria
- [ ] Create build-logic included build
- [ ] Create convention plugin for Android application
- [ ] Create convention plugin for Android library
- [ ] Create convention plugin for Compose configuration
- [ ] Create convention plugin for feature modules
- [ ] Apply plugins to all modules
- [ ] Remove duplicated configuration from module build files
- [ ] Build completes successfully

### References
- [Now in Android build-logic README](https://github.com/android/nowinandroid/blob/main/build-logic/README.md)
- [Herding Elephants - Square Blog](https://developer.squareup.com/blog/herding-elephants/)

---

## Issue 3: Implement Domain Layer with Use Cases

### Title
Add Domain Layer with Use Cases for Business Logic

### Labels
`enhancement`, `architecture`

### Priority
Medium

### Description
Introduce a domain layer with use cases following the official Android architecture guidance. Use cases encapsulate business logic, simplify ViewModels/Presenters, and improve testability.

### Current State
Business logic is scattered between repositories and presenters. No formal domain layer exists.

### Proposed Changes
Create a `:core:domain` module with use cases:

```kotlin
// Example Use Cases to Create:
class GetDevicesWithFiltersUseCase @Inject constructor(
    private val deviceRepository: AndroidDeviceRepository
) {
    operator fun invoke(filters: DeviceFilters): Flow<List<DeviceInfo>> {
        // Combine filtering, sorting, and transformation logic
    }
}

class GetDeviceStatisticsUseCase @Inject constructor(
    private val deviceRepository: AndroidDeviceRepository
) {
    operator fun invoke(): Flow<DeviceStats> {
        // Calculate statistics from device data
    }
}

class SearchDevicesUseCase @Inject constructor(
    private val deviceRepository: AndroidDeviceRepository
) {
    operator fun invoke(query: String): Flow<List<DeviceInfo>> {
        // Search with debouncing and transformation
    }
}
```

### Acceptance Criteria
- [ ] Create `:core:domain` module
- [ ] Create `GetDevicesUseCase` for devices list
- [ ] Create `GetDeviceByIdUseCase` for device details
- [ ] Create `SearchDevicesUseCase` for search functionality
- [ ] Create `GetDeviceStatisticsUseCase` for statistics
- [ ] Create `GetFilteredDevicesUseCase` for filtered queries
- [ ] Update presenters to use use cases
- [ ] Add unit tests for each use case

### References
- [Android Domain Layer](https://developer.android.com/topic/architecture/domain-layer)
- [Now in Android Domain Module](https://github.com/android/nowinandroid/tree/main/core/domain)

---

## Issue 4: Add Screenshot Testing with Roborazzi

### Title
Implement Screenshot Testing with Roborazzi

### Labels
`enhancement`, `testing`, `quality`

### Priority
Medium

### Description
Add screenshot testing using Roborazzi to ensure UI consistency across changes. Screenshot tests verify that UI renders correctly on different screen sizes and configurations.

### Current State
No screenshot testing infrastructure exists. UI changes are manually verified.

### Proposed Changes
1. Add Roborazzi dependencies and configuration
2. Create screenshot tests for key screens
3. Set up CI workflow for screenshot verification

### Files to Create
```kotlin
// DevicesScreenScreenshotTests.kt
class DevicesScreenScreenshotTests {
    @Test
    fun devicesScreen_populated() {
        // Capture screenshot with populated data
    }
    
    @Test
    fun devicesScreen_empty() {
        // Capture screenshot with empty state
    }
    
    @Test
    fun devicesScreen_loading() {
        // Capture screenshot with loading state
    }
}

// DeviceDetailsScreenScreenshotTests.kt
class DeviceDetailsScreenScreenshotTests {
    @Test
    fun deviceDetailsScreen() {
        // Capture screenshot of device details
    }
}
```

### Acceptance Criteria
- [ ] Add Roborazzi to version catalog and build configuration
- [ ] Configure Roborazzi in gradle properties
- [ ] Create screenshot tests for DevicesScreen
- [ ] Create screenshot tests for DeviceDetailsScreen
- [ ] Create screenshot tests for StatisticsScreen
- [ ] Record baseline screenshots
- [ ] Document screenshot testing workflow in README

### References
- [Roborazzi GitHub](https://github.com/takahirom/roborazzi)
- [Now in Android Screenshot Tests](https://github.com/android/nowinandroid/blob/main/app/src/testDemo/kotlin/com/google/samples/apps/nowinandroid/ui/NiaAppScreenSizesScreenshotTests.kt)

---

## Issue 5: Implement Baseline Profiles for Performance

### Title
Add Baseline Profiles for Improved App Startup Performance

### Labels
`enhancement`, `performance`

### Priority
Medium

### Description
Implement baseline profiles to enable AOT compilation of critical user paths, significantly improving app startup time and reducing jank.

### Current State
No baseline profiles exist. The app relies on default JIT compilation behavior.

### Proposed Changes
1. Create a `:benchmarks` module
2. Implement `BaselineProfileGenerator` test
3. Generate and include baseline profile in app

### Files to Create
```kotlin
// benchmarks/src/main/kotlin/BaselineProfileGenerator.kt
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = rule.collect(
        packageName = "dev.hossain.devicecatalog"
    ) {
        // Navigate through critical user paths
        pressHome()
        startActivityAndWait()
        
        // Scroll through device list
        device.waitForIdle()
        
        // Open device details
        // Navigate to statistics
    }
}
```

### Acceptance Criteria
- [ ] Create `:benchmarks` module
- [ ] Add Macrobenchmark dependencies
- [ ] Implement BaselineProfileGenerator test
- [ ] Generate baseline profile
- [ ] Include baseline profile in app/src/main/baseline-prof.txt
- [ ] Add benchmark build variant
- [ ] Document baseline profile regeneration process

### References
- [Baseline Profiles Documentation](https://developer.android.com/studio/profile/baselineprofiles)
- [Now in Android Benchmarks](https://github.com/android/nowinandroid/tree/main/benchmarks)

---

## Issue 6: Optimize Gradle Build Performance

### Title
Optimize Gradle Build Performance with Advanced Configuration

### Labels
`enhancement`, `build`, `performance`

### Priority
Low

### Description
Apply advanced Gradle performance optimizations from Now in Android to reduce build times and improve developer experience.

### Current State
Basic Gradle configuration with default settings.

### Proposed Changes
Update `gradle.properties` with optimized settings:

```properties
# JVM arguments with optimized GC settings
org.gradle.jvmargs=-Dfile.encoding=UTF-8 -XX:+UseG1GC -XX:SoftRefLRUPolicyMSPerMB=1 -XX:ReservedCodeCacheSize=256m -XX:+HeapDumpOnOutOfMemoryError -Xmx4g -Xms4g

# Kotlin Daemon arguments
kotlin.daemon.jvmargs=-Dfile.encoding=UTF-8 -XX:+UseG1GC -XX:SoftRefLRUPolicyMSPerMB=1 -XX:ReservedCodeCacheSize=320m -XX:+HeapDumpOnOutOfMemoryError -Xmx4g -Xms4g

# Enable parallel execution
org.gradle.parallel=true

# Enable configuration caching
org.gradle.configuration-cache=true
org.gradle.configuration-cache.parallel=true

# Disable unused build features
android.defaults.buildfeatures.resvalues=false
android.defaults.buildfeatures.shaders=false
```

### Acceptance Criteria
- [ ] Update gradle.properties with optimized JVM arguments
- [ ] Enable parallel execution
- [ ] Enable configuration caching
- [ ] Disable unused build features
- [ ] Verify build completes successfully with new settings
- [ ] Document performance improvements

### References
- [Now in Android gradle.properties](https://github.com/android/nowinandroid/blob/main/gradle.properties)
- [Gradle Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html)

---

## Issue 7: Create Dedicated Design System Module

### Title
Extract Design System to Dedicated Module

### Labels
`enhancement`, `architecture`, `ui`

### Priority
Medium

### Description
Create a dedicated `:core:designsystem` module containing all theme, icons, and reusable UI components. This enables design consistency, faster builds, and easier design system updates.

### Current State
Theme, colors, and typography are defined in `app/src/main/java/dev/hossain/devicecatalog/ui/theme/`. No centralized design system exists.

### Proposed Changes
Create `:core:designsystem` module with:

```
:core:designsystem/
├── src/main/kotlin/
│   └── dev/hossain/devicecatalog/designsystem/
│       ├── component/
│       │   ├── DeviceCatalogButton.kt
│       │   ├── DeviceCatalogCard.kt
│       │   ├── DeviceCatalogTopAppBar.kt
│       │   ├── DeviceCatalogLoadingWheel.kt
│       │   └── DeviceCatalogBackground.kt
│       ├── icon/
│       │   └── DeviceCatalogIcons.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           ├── Type.kt
│           └── Gradient.kt
```

### Acceptance Criteria
- [ ] Create `:core:designsystem` module
- [ ] Move Theme, Color, Type from app module
- [ ] Create DeviceCatalogIcons object with all app icons
- [ ] Create reusable button components
- [ ] Create reusable card components
- [ ] Create reusable TopAppBar component
- [ ] Create loading indicator components
- [ ] Update all features to use design system module
- [ ] Add preview annotations for design system components

### References
- [Now in Android Design System](https://github.com/android/nowinandroid/tree/main/core/designsystem)
- [Material 3 Design System](https://m3.material.io/)

---

## Issue 8: Implement Test Double Strategy (Fakes over Mocks)

### Title
Enhance Testing Infrastructure with Test Doubles (Fakes)

### Labels
`enhancement`, `testing`, `quality`

### Priority
Medium

### Description
Following Now in Android's testing strategy, implement test doubles (fakes) instead of mocks for repositories and data sources. Fakes provide more realistic tests and exercise more production code.

### Current State
Limited testing infrastructure. Some unit tests exist but no formal fake implementation strategy.

### Proposed Changes
Create a `:core:testing` module with:

1. Fake repositories
2. Fake data sources
3. Test utilities
4. Test rule classes

```kotlin
// FakeAndroidDeviceRepository.kt
class FakeAndroidDeviceRepository : AndroidDeviceRepository {
    private val devices = MutableStateFlow<List<DeviceInfo>>(emptyList())
    
    // Test hook to set device data
    fun setDevices(newDevices: List<DeviceInfo>) {
        devices.value = newDevices
    }
    
    override fun getAllDevices(): Flow<List<DeviceInfo>> = devices
    // Implement other methods...
}

// TestDispatcherRule.kt
class TestDispatcherRule : TestRule {
    val testDispatcher = StandardTestDispatcher()
    // Configure dispatchers for testing
}
```

### Acceptance Criteria
- [ ] Create `:core:testing` module
- [ ] Create FakeAndroidDeviceRepository
- [ ] Create FakeDeviceDao
- [ ] Create TestDispatcherRule
- [ ] Create sample device data factory for tests
- [ ] Update existing tests to use fakes
- [ ] Add tests for presenters using fakes
- [ ] Document testing strategy in README

### References
- [Now in Android Testing](https://github.com/android/nowinandroid#testing)
- [Android Test Doubles](https://developer.android.com/training/testing/fundamentals/test-doubles)

---

## Issue 9: Add Analytics Module

### Title
Create Analytics Module for Event Tracking

### Labels
`enhancement`, `architecture`, `feature`

### Priority
Low

### Description
Create a `:core:analytics` module with an abstraction for analytics event tracking. This enables analytics integration while keeping the implementation flexible.

### Current State
No analytics infrastructure exists.

### Proposed Changes
Create `:core:analytics` module with:

```kotlin
// AnalyticsEvent.kt
sealed interface AnalyticsEvent {
    val type: String
    val extras: List<Param>
    
    data class Param(val key: String, val value: String)
    
    // Screen views
    data class ScreenView(val screenName: String) : AnalyticsEvent
    
    // Device actions
    data class DeviceViewed(val deviceId: String, val manufacturer: String) : AnalyticsEvent
    data class DeviceSearched(val query: String, val resultCount: Int) : AnalyticsEvent
    data class FilterApplied(val filterType: String, val filterValue: String) : AnalyticsEvent
}

// AnalyticsHelper.kt
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}

// StubAnalyticsHelper.kt (for debug builds)
class StubAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        Timber.d("Analytics: $event")
    }
}
```

### Acceptance Criteria
- [ ] Create `:core:analytics` module
- [ ] Define AnalyticsEvent sealed interface
- [ ] Define AnalyticsHelper interface
- [ ] Create StubAnalyticsHelper for debug builds
- [ ] Add analytics events for key user actions
- [ ] Integrate analytics in feature modules
- [ ] Document analytics events

### References
- [Now in Android Analytics](https://github.com/android/nowinandroid/tree/main/core/analytics)

---

## Issue 10: Add Compose Compiler Metrics and Reports

### Title
Enable Compose Compiler Metrics and Reports for Performance Analysis

### Labels
`enhancement`, `performance`, `developer-experience`

### Priority
Low

### Description
Enable Compose compiler metrics and reports to analyze composable stability and identify recomposition issues.

### Current State
No Compose compiler metrics configured.

### Proposed Changes
1. Add gradle properties for enabling metrics
2. Create task for generating reports
3. Document how to interpret reports

Add to `gradle.properties`:
```properties
# Enable with: ./gradlew assembleRelease -PenableComposeCompilerMetrics=true -PenableComposeCompilerReports=true
```

Add to app `build.gradle.kts`:
```kotlin
composeCompiler {
    if (project.findProperty("enableComposeCompilerMetrics") == "true") {
        val metricsFolder = File(project.buildDir, "compose-metrics")
        metricsDestination.set(metricsFolder)
    }
    if (project.findProperty("enableComposeCompilerReports") == "true") {
        val reportsFolder = File(project.buildDir, "compose-reports")
        reportsDestination.set(reportsFolder)
    }
}
```

### Acceptance Criteria
- [ ] Add gradle properties for metrics and reports
- [ ] Configure compose compiler in build files
- [ ] Generate initial metrics and reports
- [ ] Document how to run and interpret metrics
- [ ] Fix any stability issues identified

### References
- [Compose Stability Explained](https://medium.com/androiddevelopers/jetpack-compose-stability-explained-79c10db270c8)
- [Now in Android Compose Config](https://github.com/android/nowinandroid/blob/main/compose_compiler_config.conf)

---

## Issue 11: Implement Offline-First Repository Pattern

### Title
Enhance Repository with Offline-First Pattern and Sync Strategy

### Labels
`enhancement`, `architecture`, `data`

### Priority
Medium

### Description
Enhance the repository layer to follow an offline-first pattern with proper synchronization strategy, similar to Now in Android's approach.

### Current State
Repository reads from local database. No formal offline-first strategy or sync reconciliation exists.

### Proposed Changes
1. Define Synchronizer interface
2. Implement sync conflict resolution
3. Add sync status monitoring
4. Integrate with WorkManager for background sync

```kotlin
// Synchronizer.kt
interface Synchronizer {
    suspend fun sync(): Boolean
    fun getLastSyncTime(): Instant?
}

// OfflineFirstDeviceRepository.kt
class OfflineFirstDeviceRepository @Inject constructor(
    private val deviceDao: AndroidDeviceDao,
    private val networkDataSource: DeviceNetworkDataSource?,
) : AndroidDeviceRepository, Synchronizer {
    
    override suspend fun sync(): Boolean {
        return try {
            val networkDevices = networkDataSource?.getDevices() ?: return true
            deviceDao.upsertDevices(networkDevices)
            true
        } catch (e: Exception) {
            Timber.e(e, "Sync failed")
            false
        }
    }
    
    // Repository methods always read from local DB
    override fun getAllDevices(): Flow<List<DeviceInfo>> {
        return deviceDao.getAllDevicesWithRelations()
            .map { it.map { device -> device.toModel() } }
    }
}
```

### Acceptance Criteria
- [ ] Define Synchronizer interface
- [ ] Create Syncable interface for repositories
- [ ] Implement sync in DeviceRepository
- [ ] Add SyncWorker for background synchronization
- [ ] Add sync status observable
- [ ] Handle sync conflicts appropriately
- [ ] Add tests for sync scenarios

### References
- [Now in Android Architecture - Data Layer](https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md#data-layer)
- [Android Offline-First](https://developer.android.com/topic/architecture/data-layer/offline-first)

---

## Issue 12: Add UI State Modeling with Sealed Hierarchies

### Title
Standardize UI State Modeling with Sealed Hierarchies

### Labels
`enhancement`, `architecture`, `ui`

### Priority
Low

### Description
Standardize UI state modeling across all features using sealed hierarchies, ensuring all possible states are handled and the UI always represents the underlying data.

### Current State
UI states may not be consistently modeled across features.

### Proposed Changes
Create standardized UI state patterns:

```kotlin
// UiState.kt in core:ui
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}

// DevicesUiState.kt
sealed interface DevicesUiState {
    data object Loading : DevicesUiState
    data class Success(
        val devices: List<DeviceInfo>,
        val searchQuery: String,
        val filters: DeviceFilters,
        val isRefreshing: Boolean
    ) : DevicesUiState
    data class Error(val message: String) : DevicesUiState
}

// Extension function for state handling
@Composable
fun <T> UiState<T>.OnState(
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (String) -> Unit
) {
    when (this) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(message)
    }
}
```

### Acceptance Criteria
- [ ] Create base UiState sealed interface in core:ui
- [ ] Create DevicesUiState sealed interface
- [ ] Create DeviceDetailsUiState sealed interface
- [ ] Create StatisticsUiState sealed interface
- [ ] Update all presenters to emit sealed states
- [ ] Update all screens to handle all states
- [ ] Add tests for state transitions

### References
- [Now in Android UI State](https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md#ui-layer)
- [UI State Production](https://developer.android.com/topic/architecture/ui-layer#state-production)

---

## Summary

| Issue | Title | Priority | Complexity |
|-------|-------|----------|------------|
| 1 | Multi-Module Architecture | High | High |
| 2 | Build Logic Convention Plugins | Medium | Medium |
| 3 | Domain Layer with Use Cases | Medium | Medium |
| 4 | Screenshot Testing with Roborazzi | Medium | Medium |
| 5 | Baseline Profiles | Medium | Low |
| 6 | Gradle Build Optimization | Low | Low |
| 7 | Design System Module | Medium | Medium |
| 8 | Test Doubles Strategy | Medium | Medium |
| 9 | Analytics Module | Low | Low |
| 10 | Compose Compiler Metrics | Low | Low |
| 11 | Offline-First Repository | Medium | Medium |
| 12 | UI State Sealed Hierarchies | Low | Low |

### Recommended Order of Implementation

1. **Phase 1 - Foundation (Issues 6, 2)**
   - Start with Gradle optimizations for immediate benefit
   - Set up build logic for consistent configuration

2. **Phase 2 - Architecture (Issues 1, 7)**
   - Implement multi-module architecture
   - Extract design system

3. **Phase 3 - Data Layer (Issues 3, 11, 12)**
   - Add domain layer with use cases
   - Enhance repository pattern
   - Standardize UI states

4. **Phase 4 - Quality (Issues 8, 4, 5)**
   - Implement test doubles
   - Add screenshot testing
   - Generate baseline profiles

5. **Phase 5 - Observability (Issues 9, 10)**
   - Add analytics module
   - Enable compose compiler metrics
