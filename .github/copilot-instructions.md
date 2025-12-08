# Copilot Instructions - Android Device Universe App

## Project Overview

This is an Android application that provides a catalog of Android devices with detailed specifications. The app uses Room database for data persistence with proper one-to-many relationships.
Check the [PRD](../project-resources/PRD.md) document for detailed requirements and features.

## Architecture & Tech Stack

### Architecture Pattern
This app follows **multi-module architecture** based on [Now in Android](https://github.com/android/nowinandroid) best practices:
- **Modular design**: Separation between core, feature, and app layers
- **Convention plugins**: Consistent build configuration via `build-logic` module
- **Offline-first**: Data layer with repository pattern
- **Unidirectional data flow**: Circuit Presenter/UI pattern

### Core Technologies
- **Language**: Kotlin with Jetpack Compose
- **Database**: Room with one-to-many relationships
- **Dependency Injection**: Metro (compile-time DI)
- **UI Architecture**: Circuit (Compose-driven architecture)
- **Build System**: Gradle with Version Catalog + Convention Plugins
- **Logging**: Timber

### Key Libraries
- **Room Database**: For data persistence with proper entity relationships
- **Jetpack Compose**: Modern Android UI toolkit
- **Metro**: Compile-time dependency injection framework
- **Paging 3**: For efficient list loading
- **Circuit**: Slack's Compose-driven architecture with code generation
- **Timber**: Logging framework
- **Work Manager**: Background processing

## Database Architecture

### Room Database Approach

The project uses Room database with proper entity relationships:

1. **Room** entities define the database schema with annotations
2. **Room** DAO provides type-safe database access with relationship queries
3. Database schema is exported for validation (version 2 with auto-migrations)

### Database Schema

#### Main Entity
- **`device`** table: Core device information with `_id` as primary key

#### Related Entities (One-to-Many)
- **`device_abi`**: Supported ABIs per device
- **`device_opengl`**: OpenGL versions per device  
- **`device_screen_density`**: Screen densities per device
- **`device_screen_size`**: Screen sizes per device
- **`device_sdk`**: SDK versions per device

All related tables use composite primary keys: `(device_id, value)` and have foreign key relationships with CASCADE delete/update.

### Room Entity Structure

```kotlin
@Entity(
    tableName = "table_name",
    primaryKeys = ["device_id", "value_column"],
    foreignKeys = [ForeignKey(...)],
    indices = [Index(value = ["device_id"])]
)
```

### One-to-Many Relationships

Use `AndroidDeviceWithRelations` for querying devices with all related data:

```kotlin
@Transaction
@Query("SELECT * FROM device")
fun getAllDevicesWithRelations(): Flow<List<AndroidDeviceWithRelations>>
```

## Project Structure

The app follows a **multi-module architecture** with clear separation of concerns:

### App Module (`:app`)
Main application module that:
- Configures Circuit navigation and screens
- Sets up dependency injection with Metro
- Contains example/sample files for reference (circuit screens, workers)
- Note: May contain some legacy code being migrated to core/feature modules

### Core Modules
Foundation modules used across features:

#### `:core:database`
Room database layer with entity relationships
- `AppDatabase.kt` - Room database configuration with auto-migrations
- `AndroidDeviceDao.kt` - Data access operations with relationship queries
- `AndroidDeviceEntity.kt` - Main device entity
- `AndroidDeviceWithRelations.kt` - Data class for one-to-many relationships
- `DeviceAbi.kt`, `DeviceOpenGl.kt`, `DeviceScreenDensity.kt`, etc. - Related entities
- **Location**: `core/database/src/main/kotlin/dev/hossain/devicecatalog/core/database/`
- **Note**: Database is preloaded with the app

#### `:core:data`
Repository layer with business logic
- `AndroidDeviceRepository.kt` - Repository pattern with Timber logging and Metro DI
- Provides data to feature modules
- **Location**: `core/data/src/main/kotlin/dev/hossain/devicecatalog/core/data/`

#### `:core:model`
Domain models and data classes
- `DeviceInfo.kt` - Core device information model
- Shared across all modules
- **Location**: `core/model/src/main/kotlin/dev/hossain/devicecatalog/core/model/`

#### `:core:designsystem`
Material 3 design system and reusable UI components
- `Theme.kt`, `Color.kt`, `Type.kt` - Material 3 theming
- `DeviceCatalogIcons.kt` - Centralized icon management
- `DeviceCatalogButton.kt` - Consistent button components
- `DeviceCatalogCard.kt` - Card components
- `DeviceCatalogTopAppBar.kt` - Top app bar component
- `DeviceCatalogLoadingWheel.kt` - Loading indicators
- `DeviceCatalogBackground.kt` - Background components
- **Location**: `core/designsystem/src/main/kotlin/dev/hossain/devicecatalog/core/designsystem/`

#### `:core:common`
Shared utilities and common code
- `FeatureFlags.kt` - Feature flag management
- `PreferenceKeys.kt` - Shared preference keys
- `PerformanceMonitor.kt` - Performance monitoring utilities
- **Location**: `core/common/src/main/kotlin/dev/hossain/devicecatalog/core/common/`

#### `:core:di`
Dependency injection infrastructure for multi-module setup
- `ActivityKey.kt` - MapKey for type-safe Activity injection
- `WorkerKey.kt` - MapKey for WorkManager integration
- `UiMultibindings.kt` - Multibinding interface for UI components
- Centralizes DI annotations and utilities used across modules
- **Location**: `core/di/src/main/kotlin/dev/hossain/devicecatalog/core/di/`
- **Best Practice**: Use `@ContributesTo` interfaces for multibindings, similar to [CatchUp](https://github.com/ZacSweers/CatchUp)

#### `:core:ui`
Common UI components and utilities (currently being populated)
- **Location**: `core/ui/src/main/kotlin/dev/hossain/devicecatalog/core/ui/`

### Feature Modules
Self-contained features with their own screens, presenters, and UI:

#### `:feature:devices`
Device list screen with search, filters, and pagination
- `DevicesScreen.kt` - Circuit screen definition
- `DevicesPresenter.kt` - Business logic and state management
- `DevicesUi.kt` - Composable UI
- `components/` - Feature-specific UI components (DeviceCard, FilterBottomSheet, etc.)
- **Location**: `feature/devices/src/main/kotlin/dev/hossain/devicecatalog/feature/devices/`

#### `:feature:devicedetails`
Device details screen showing full specifications
- `DeviceDetailsScreen.kt` - Circuit screen definition
- `DeviceDetailsPresenter.kt` - Business logic
- `DeviceDetailsUi.kt` - Composable UI
- **Location**: `feature/devicedetails/src/main/kotlin/dev/hossain/devicecatalog/feature/devicedetails/`

#### `:feature:statistics`
Statistics and about screens
- `DeviceStatsScreen.kt`, `DeviceStatsPresenter.kt`, `DeviceStatsUi.kt` - Statistics screen
- `AboutScreen.kt`, `AboutPresenter.kt`, `AboutUi.kt` - About screen
- `components/` - Feature-specific components
- **Location**: `feature/statistics/src/main/kotlin/dev/hossain/devicecatalog/feature/statistics/`

#### `:feature:settings`
Developer settings and preferences
- `DeveloperSettingsScreenCircuit.kt` - Circuit screen definition
- `DeveloperSettingsPresenter.kt` - Business logic
- `DeveloperSettingsUi.kt` - Composable UI
- **Location**: `feature/settings/src/main/kotlin/dev/hossain/devicecatalog/feature/settings/`

#### `:feature:quizhub`
Quiz hub screen with available quizzes
- `QuizHubScreen.kt` - Circuit screen definition
- `QuizHubPresenter.kt` - Business logic
- `QuizHubUi.kt` - Composable UI
- `components/` - Feature-specific UI components (QuizTile, ComingSoonBadge)
- **Location**: `feature/quizhub/src/main/kotlin/dev/hossain/devicecatalog/feature/quizhub/`

#### `:feature:phonequiz`
Interactive phone quiz feature with manufacturer selection
- `selection/` - Manufacturer selection screen
  - `ManufacturerSelectionScreen.kt`, `ManufacturerSelectionPresenter.kt`, `ManufacturerSelectionUi.kt`
- `quiz/` - Quiz screen
  - `QuizScreen.kt`, `QuizPresenter.kt`, `QuizUi.kt`
- `results/` - Quiz results screen
  - `QuizResultsScreen.kt`, `QuizResultsPresenter.kt`, `QuizResultsUi.kt`
- `QuizService.kt` - Quiz business logic service
- `QuizModels.kt` - Data models for quiz
- `components/` - Feature-specific UI components
- **Location**: `feature/phonequiz/src/main/kotlin/dev/hossain/devicecatalog/feature/phonequiz/`

#### `:feature:brandchallenge`
Brand recognition challenge feature
- `BrandChallengeScreen.kt` - Circuit screen definition
- `BrandChallengePresenter.kt` - Business logic
- `BrandChallengeUi.kt` - Composable UI
- `BrandChallengeService.kt` - Challenge business logic
- `BrandChallengeModels.kt` - Data models
- **Location**: `feature/brandchallenge/src/main/kotlin/dev/hossain/devicecatalog/feature/brandchallenge/`

#### `:feature:statsexplorer`
Advanced statistics exploration feature
- `StatsExplorerScreen.kt` - Circuit screen definition
- `StatsExplorerPresenter.kt` - Business logic
- `StatsExplorerUi.kt` - Composable UI
- `components/` - Feature-specific components (ChartView, StatCard, InsightCard)
- **Location**: `feature/statsexplorer/src/main/kotlin/dev/hossain/devicecatalog/feature/statsexplorer/`

#### `:feature:devicecomparison`
Side-by-side device comparison feature
- `DeviceComparisonScreen.kt` - Circuit screen definition
- `DeviceComparisonPresenter.kt` - Business logic
- `DeviceComparisonUi.kt` - Composable UI
- `components/` - Feature-specific components (ComparisonTable, DeviceSelector, SpecRow)
- **Location**: `feature/devicecomparison/src/main/kotlin/dev/hossain/devicecatalog/feature/devicecomparison/`

#### `:feature:dreamphone`
Dream phone builder/configurator feature
- `DreamPhoneScreen.kt` - Circuit screen definition
- `DreamPhonePresenter.kt` - Business logic
- `DreamPhoneUi.kt` - Composable UI
- **Location**: `feature/dreamphone/src/main/kotlin/dev/hossain/devicecatalog/feature/dreamphone/`

### Build Logic (`:build-logic`)
Gradle convention plugins for consistent configuration:
- `AndroidApplicationConventionPlugin.kt` - App module configuration
- `AndroidLibraryConventionPlugin.kt` - Library module configuration
- `AndroidFeatureConventionPlugin.kt` - Feature module configuration (Compose + Metro)
- `AndroidComposeConventionPlugin.kt` - Compose-specific configuration
- `JvmLibraryConventionPlugin.kt` - Pure Kotlin/JVM modules
- **Location**: `build-logic/convention/src/main/kotlin/`

### Module Dependencies
- Feature modules depend on: `core:common`, `core:data`, `core:database`, `core:designsystem`, `core:di`, `core:model`
- Core modules have minimal dependencies between each other
- App module depends on all core and feature modules
- `core:di` provides DI infrastructure used by app and can be used by any module needing DI annotations

## Development Guidelines

### Working with Multi-Module Architecture

1. **Module Independence**: Feature modules should be independent and not depend on other feature modules (except for navigation)
2. **Shared Code**: Place shared code in appropriate core modules:
   - `core:common` - Utilities, constants, feature flags
   - `core:model` - Domain models and data classes
   - `core:designsystem` - UI components and theme
   - `core:database` - Database entities and DAOs
   - `core:data` - Repositories and data sources
3. **Avoid Circular Dependencies**: Core modules should not depend on feature modules
4. **Convention Plugins**: Always use convention plugins instead of duplicating build configuration
5. **Namespace Convention**: Use `dev.hossain.devicecatalog.core.<module>` or `dev.hossain.devicecatalog.feature.<module>`

### Database Operations

1. **Always use transactions** for multi-table operations
2. **Use relationship queries** instead of manual joins
3. **Log operations** with Timber for debugging
4. **Handle foreign key constraints** properly

### Metro Dependency Injection Best Practices

The app uses [Metro](https://zacsweers.github.io/metro/), a compile-time dependency injection framework, following best practices from [CatchUp](https://github.com/ZacSweers/CatchUp).

#### Core DI Setup

1. **`:core:di` Module**: Centralized DI infrastructure
   - `ActivityKey` and `WorkerKey`: MapKey annotations for type-safe multibinding
   - `UiMultibindings`: Interface for UI component multibindings
   - Import from `dev.hossain.devicecatalog.core.di.*` in all modules

2. **Constructor Injection** (Preferred):
   ```kotlin
   @Inject
   class AndroidDeviceRepository(
       private val deviceDao: AndroidDeviceDao,
   )
   ```

3. **Assisted Injection** (for Circuit Presenters):
   ```kotlin
   @AssistedInject
   class DevicesPresenter(
       @Assisted private val navigator: Navigator,
       private val deviceRepository: AndroidDeviceRepository,
   ) : Presenter<DevicesScreen.State>
   ```

4. **Circuit Integration** (for screens):
   ```kotlin
   @CircuitInject(DevicesScreen::class, AppScope::class)
   class DevicesPresenter(...)
   
   @CircuitInject(DevicesScreen::class, AppScope::class)
   @Composable
   fun DevicesUi(state: DevicesScreen.State, modifier: Modifier = Modifier)
   ```

#### Multi-Module DI Patterns

1. **@ContributesTo Pattern**: Use for multibindings in shared modules
   ```kotlin
   @ContributesTo(AppScope::class)
   interface DataMultibindings {
       @Multibinds
       fun repositories(): Set<Repository>
   }
   ```

2. **Activity Contribution**:
   ```kotlin
   @ActivityKey(MainActivity::class)
   @ContributesIntoMap(AppScope::class, binding = binding<Activity>())
   @Inject
   class MainActivity(private val circuit: Circuit) : ComponentActivity()
   ```

3. **Worker Contribution**:
   ```kotlin
   @WorkerKey(DeviceSyncWorker::class)
   @ContributesIntoMap(AppScope::class, binding = binding<WorkerInstanceFactory<*>>())
   @AssistedFactory
   abstract class Factory : WorkerInstanceFactory<DeviceSyncWorker>
   ```

4. **BindingContainer** (for app module):
   ```kotlin
   @BindingContainer
   @ContributesTo(AppScope::class)
   object DatabaseBindings {
       @Provides
       @SingleIn(AppScope::class)
       fun provideDatabase(context: Context): AppDatabase = ...
   }
   ```

#### Key Metro Annotations

- `@Inject`: Constructor injection for regular classes
- `@AssistedInject`: Inject with some runtime parameters
- `@Assisted`: Mark runtime parameters in assisted injection
- `@CircuitInject`: Circuit-specific injection (generates Factory)
- `@ContributesTo(scope)`: Contribute bindings to a scope automatically
- `@ContributesIntoMap(scope, binding)`: Contribute to a multibinding map
- `@Multibinds`: Declare an empty multibinding
- `@SingleIn(scope)`: Scope a binding to a lifecycle
- `@Provides`: Provide a dependency (in interfaces or objects)
- `@BindingContainer`: Mark a class/object as a binding container

#### DI Guidelines

1. **No @Module or @Component**: Metro uses `@DependencyGraph` and auto-discovery
2. **Prefer Constructor Injection**: Simpler and more testable
3. **Use @SingleIn wisely**: Only for true singletons (Database, WorkManager, etc.)
4. **Multibindings in core modules**: Define interfaces with `@ContributesTo` in shared modules
5. **Feature Independence**: Feature modules contribute independently without knowing about each other

### Adding New Features

When adding new features, follow the multi-module architecture:

1. **Create Feature Module**: Create a new module under `feature/` directory
   - Use `:feature:featurename` naming convention
   - Apply `devicecatalog.android.feature` convention plugin in `build.gradle.kts`
   - Add dependencies to required core modules

2. **Implement Circuit Screen**: Follow the Screen/Presenter/UI pattern
   - `FeatureScreen.kt` - Define the screen with `@Parcelize` and `@CircuitInject`
   - `FeaturePresenter.kt` - Implement business logic and state management
   - `FeatureUi.kt` - Implement the composable UI
   - See existing features like `:feature:devices` as reference

3. **Use Design System**: Always use components from `:core:designsystem`
   - Use `DeviceCatalogButton`, `DeviceCatalogCard`, etc.
   - Follow Material 3 theming with `MaterialTheme.colorScheme`
   - Never hardcode colors or create custom themes

4. **Database Changes**: 
   - Update Room entities in `:core:database` and increment database version
   - Add migrations if needed (auto-migrations are configured)
   - Update repository in `:core:data` if needed

5. **Add to Navigation**: Register screen in app module's Circuit configuration

6. **Testing**: 
   - Use Room's testing utilities for database tests
   - Use Circuit's `circuit-test` library with `FakeNavigator` for screen tests
   - Use fake implementations over mocks

#### Example template files
The app module contains example/sample files for reference implementations:
* `app/src/main/java/dev/hossain/devicecatalog/circuit/ExampleEmailDetailsScreen.kt` - Example Circuit screen
* `app/src/main/java/dev/hossain/devicecatalog/circuit/ExampleInboxScreen.kt` - Example Circuit screen with navigation
* `app/src/main/java/dev/hossain/devicecatalog/circuit/overlay/AppInfoOverlay.kt` - Example bottomsheet using Circuit overlay
* `app/src/main/java/dev/hossain/devicecatalog/work/SampleWorker.kt` - Example WorkManager worker for background tasks

**Note**: For production features, create proper feature modules under `feature/` instead of adding to app module.

### Code Style

- **Kotlin Style Guide**: Follow [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- **Formatting**: Enforced by ktlint plugin
- **Naming**:
  - Classes: PascalCase
  - Functions/Properties: camelCase
  - Constants: SCREAMING_SNAKE_CASE
  - Composables: PascalCase (like classes)

#### Logging with Timber
```kotlin
Timber.d("Debug message with context")
Timber.i("Info about successful operation")
Timber.w("Warning about potential issue")
Timber.e(exception, "Error with context")
```

#### Repository Pattern
```kotlin
suspend fun operationName(params): ReturnType {
    Timber.d("Starting operation with: $params")
    return try {
        val result = dao.operation(params)
        Timber.i("Successfully completed operation")
        result
    } catch (e: Exception) {
        Timber.e(e, "Failed to complete operation")
        throw e
    }
}
```

#### Relationship Queries
```kotlin
@Transaction
@Query("SELECT * FROM device WHERE condition")
fun getDevicesWithRelations(params): Flow<List<AndroidDeviceWithRelations>>
```

### Material You / Material 3 Guidelines

**All screens and UI components MUST be Material You compatible:**

1. **Use Material 3 Components**:
   - Use `androidx.compose.material3.*` components (NOT `material` or `material2`)
   - Prefer Material 3 equivalents: `Button`, `Card`, `TextField`, `TopAppBar`, etc.
   - Use `ListItem` for list entries with proper leading/trailing content

2. **Theme-Aware Colors**:
   - **NEVER use hardcoded colors** (e.g., `Color(0xFF4CAF50)`, `Color.Red`)
   - Always use `MaterialTheme.colorScheme.*` for colors:
     - `primary`, `onPrimary` - Main brand colors
     - `primaryContainer`, `onPrimaryContainer` - Filled components
     - `secondary`, `tertiary` - Accent colors
     - `error`, `onError` - Error states
     - `surface`, `onSurface` - Backgrounds
     - `surfaceVariant`, `onSurfaceVariant` - Alternative surfaces
   - For status indicators, use semantic color scheme tokens with alpha modifiers
   
3. **Dynamic Color Support**:
   - The app uses `dynamicColor = true` for Android 12+ wallpaper-based theming
   - All colors must work in both light and dark themes
   - Test color contrast in both theme modes

4. **Edge-to-Edge Display**:
   - Use `Modifier.padding(innerPadding)` with `Scaffold` to respect system bars
   - Status and navigation bars are transparent

5. **Typography**:
   - Use `MaterialTheme.typography.*` for all text
   - Available styles: `displayLarge/Medium/Small`, `headlineLarge/Medium/Small`, `titleLarge/Medium/Small`, `bodyLarge/Medium/Small`, `labelLarge/Medium/Small`

**Example - Good Practice**:
```kotlin
@Composable
fun GoodExample() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text = "Hello",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
```

**Example - Bad Practice** ❌:
```kotlin
// DON'T DO THIS
Card(colors = CardDefaults.cardColors(containerColor = Color.Blue)) {
    Text(text = "Hello", color = Color.White)
}
```

### Performance Guidelines

1. **Use paging** for large datasets
2. **Add indexes** on frequently queried columns
3. **Use transactions** for bulk operations
4. **Leverage relationship queries** instead of multiple database calls

### Testing Guidelines

- **Unit Tests**: Required for all repositories and data access operations
- **Assertions**: Use standard JUnit assertions (`assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, etc.)
  - The project currently uses JUnit 4 assertions
  - Keep assertions simple and readable
  - Example:
    ```kotlin
    import org.junit.Assert.*
    
    @Test
    fun `test example`() {
        val result = someFunction()
        assertEquals("expected", result)
        assertTrue(result.isNotEmpty())
    }
    ```
- **Test Doubles**: Use fakes instead of mocks when possible
  - **Fakes** are preferred: lightweight, working implementations suitable for tests (e.g., in-memory database, `FakeNavigator`)
  - **Mocks** should be avoided unless necessary: require mocking frameworks and add complexity
  - See [Android Test Doubles Guide](https://developer.android.com/training/testing/fundamentals/test-doubles) for detailed explanations
- **Coroutine Testing**: Use `kotlinx-coroutines-test` with `runTest`
- **Circuit Testing**: Use `circuit-test` library with `FakeNavigator` and `Presenter.test()` helpers
- **Test Coverage**: Aim for success cases, error cases, and edge cases

### Testing Database

1. **Room Testing**: Use `@Database(exportSchema = true)` for schema validation (already configured)
2. **In-Memory Database**: For unit tests
3. **Migration Testing**: Test schema changes thoroughly, auto-migrations are configured

## Build Configuration

### Multi-Module Architecture
The project uses a multi-module architecture with the following structure:
- **`:app`** - Main application module
- **`:core:*`** - Core/foundation modules (common, data, database, designsystem, model, ui)
- **`:feature:*`** - Feature modules (devices, devicedetails, statistics, settings)
- **`:build-logic`** - Convention plugins for build configuration

### Convention Plugins (`build-logic/convention`)
All modules use convention plugins for consistent configuration. Available plugins:

1. **`devicecatalog.android.application`** - Configure Android application modules
   - Sets up Android app configuration, signing, build types
   - Includes Kotlin and Android defaults

2. **`devicecatalog.android.library`** - Configure Android library modules
   - Sets up Android library configuration
   - Includes Kotlin and Android defaults
   - Used by core modules

3. **`devicecatalog.android.compose`** - Configure Jetpack Compose
   - Enables Compose compiler
   - Sets up Compose dependencies
   - Can generate Compose metrics and reports

4. **`devicecatalog.android.feature`** - Configure feature modules
   - Combines library + compose + Metro DI configuration
   - Standard setup for all feature modules
   - Used by `:feature:*` modules

5. **`devicecatalog.jvm.library`** - Configure pure Kotlin/JVM modules
   - For modules without Android dependencies

### Using Convention Plugins
In your module's `build.gradle.kts`:
```kotlin
plugins {
    id("devicecatalog.android.feature")  // For feature modules
    // or
    id("devicecatalog.android.library")  // For core modules
    // Add other plugins as needed
    alias(libs.plugins.ksp)
}
```

### Version Catalog (`gradle/libs.versions.toml`)
All dependencies are managed through the version catalog. When adding new dependencies:

1. Add version to `[versions]` section
2. Add library to `[libraries]` section  
3. Reference in `build.gradle.kts` as `libs.libraryName`

### Key Gradle Plugins
- `androidx.room` - Room compiler and schema export
- `ksp` - Kotlin Symbol Processing for Room and Circuit code generation
- `kotlin.compose` - Compose compiler
- `metro` - Metro dependency injection code generation
- `kotlinter` - Kotlin linting and formatting

## Common Patterns

### Database Initialization
```kotlin
// Repository is injected via Metro DI in presenters
class DevicesPresenter @Inject constructor(
    private val repository: AndroidDeviceRepository
) : Presenter<DevicesScreen.State> {
    // Use repository methods
    val devices = repository.getAllDevices()
}
```

### Inserting with Relationships
```kotlin
val device = AndroidDeviceEntity(/* device data */)
val deviceId = repository.insertDevice(device) // Handles all relationships
```

### Paging with Relationships
```kotlin
// In repository (core:data module)
fun getPagedDevices(): Flow<PagingData<AndroidDeviceWithRelations>> {
    return dao.getPagedDevices()
        .map { /* transform if needed */ }
}

// In presenter (feature module)
val pagedDevices = repository.getPagedDevices()
    .cachedIn(presenterScope)
```

## Troubleshooting

### Schema Validation Errors
1. Ensure Room entities match database schema exactly
2. Check primary key definitions
3. Verify foreign key relationships
4. Update database version and add migrations when schema changes

### Performance Issues
1. Check if proper indexes exist on foreign keys
2. Use `@Transaction` for relationship queries
3. Consider paging for large datasets

### Build Issues
1. Clean and rebuild after schema changes
2. Check KSP generated code in `build/generated/ksp/`
3. Verify version catalog references

## Dependencies to Avoid

- Raw SQLite operations (use Room instead)
- Manual relationship handling (use `@Relation`)
- Synchronous database operations (use suspend functions)
- Multiple database libraries (stick to Room)

## Development Workflow

### Before Committing

**IMPORTANT**: Always run these commands before making a commit:

```bash
# 1. Format Kotlin code (auto-fixes style issues)
./gradlew formatKotlin

# 2. Run all tests (ensures nothing is broken)
./gradlew test

# 3. Run lint checks to catch code quality issues
./gradlew lintDebug

# 4. Run debug build to ensure no build issues
./gradlew assembleDebug
```

If any command fails, fix the issues before committing.

### Changelog Maintenance

**REQUIRED**: Always update `CHANGELOG.md` when making changes following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) guidelines:

1. **Format**: Follow Keep a Changelog format
2. **Versioning**: Use [Semantic Versioning](https://semver.org/spec/v2.0.0.html) (MAJOR.MINOR.PATCH)
   - MAJOR: Incompatible API changes
   - MINOR: Add functionality in a backward compatible manner
   - PATCH: Backward compatible bug fixes
3. **Sections**: Use appropriate change types:
   - `Added` for new features
   - `Changed` for changes in existing functionality
   - `Deprecated` for soon-to-be removed features
   - `Removed` for now removed features
   - `Fixed` for any bug fixes
   - `Security` in case of vulnerabilities
4. **Unreleased Section**: Add all changes to `[Unreleased]` section first
5. **Avoid Duplicate Section Headers**: 
   - **CRITICAL**: Before adding a new section header (e.g., `### Added`, `### Changed`, `### Fixed`), **always check if that section already exists** in the `[Unreleased]` section
   - If the section header already exists, **add your entry to the existing section** rather than creating a duplicate header
   - Only create a new section header if it doesn't already exist in `[Unreleased]`
   - Example of CORRECT approach:
     ```markdown
     ## [Unreleased]
     
     ### Added
     - Existing feature A
     - NEW: Your new feature B  ← Add here, don't create another ### Added
     
     ### Fixed
     - Existing bug fix
     ```
   - Example of INCORRECT approach (DO NOT DO THIS):
     ```markdown
     ## [Unreleased]
     
     ### Added
     - Existing feature A
     
     ### Added  ← WRONG: Duplicate header
     - Your new feature B
     ```
6. **Release Process**: When releasing, move `[Unreleased]` changes to a new version section with date
7. **Format Example**:
   ```markdown
   ## [Unreleased]
   
   ### Added
   - New feature description
   
   ### Fixed
   - Bug fix description
   
   ## [1.0.1] - 2025-10-03
   
   ### Fixed
   - Previous bug fix
   ```
8. **Guidelines**:
   - Write for humans, not machines
   - Each version should have an entry
   - Group similar types of changes together
   - Use ISO 8601 date format (YYYY-MM-DD)
   - Link versions at bottom of file
   - Keep entries concise but descriptive
   - Don't dump git commit logs

**Example Workflow**:
```bash
# 1. Make code changes
# 2. Update CHANGELOG.md under [Unreleased] section
# 3. Format code
./gradlew formatKotlin
# 4. Run tests
./gradlew test
# 5. Run lint checks
./gradlew lintDebug
# 6. Commit with descriptive message
git commit -m "Add feature X

- Updated CHANGELOG.md with new feature"
```

### Release Workflow

**REQUIRED**: When creating a new release, always follow this workflow:

```bash
# 1. Create a release branch from main
git checkout -b release/X.Y.Z

# 2. Update CHANGELOG.md
#    - Move [Unreleased] changes to new version section with date
#    - Update version links at bottom of file
#    - Keep empty [Unreleased] section for future changes

# 3. Update version in app/build.gradle.kts
#    - Increment versionCode
#    - Update versionName to match release version

# 4. Run pre-commit checks
./gradlew formatKotlin
./gradlew test
./gradlew lintDebug
./gradlew assembleDebug

# 5. Commit release changes on release branch
git add CHANGELOG.md app/build.gradle.kts
git commit -m "Release X.Y.Z

- Updated CHANGELOG.md for version X.Y.Z
- Bumped version to X.Y.Z (versionCode: N)"

# 6. Create and push release tag (without 'v' prefix)
git tag -a X.Y.Z -m "Release X.Y.Z"
git push origin release/X.Y.Z
git push origin X.Y.Z

# 7. Merge release branch to main via Pull Request
#    - Create PR from release/X.Y.Z to main
#    - Review and merge the release PR
#    - Delete release branch after merge

# 8. Create GitHub Release
#    - Use the tag X.Y.Z
#    - Copy changelog entries as release notes
#    - Attach APK/AAB artifacts if needed
```

**Why use a release branch?**
- Keeps main branch stable during release preparation
- Allows for last-minute fixes on the release branch without blocking main
- Provides clear separation between development and release preparation
- Enables easy rollback if issues are found during release testing
- Follows gitflow-style branching model for releases

### Git Tagging Convention

When creating release tags, always use the version number **without** the `v` prefix:

```bash
# Correct
git tag -a 1.1.0 -m "Release 1.1.0"
git push origin 1.1.0

# Incorrect - Do NOT use 'v' prefix
git tag -a v1.1.0 -m "Release v1.1.0"  # ❌ Wrong
```

This maintains consistency with the project's versioning scheme and ensures compatibility with release automation tools.

### Common Gradle Tasks

```bash
# Build the entire project (all modules)
./gradlew build

# Build specific module
./gradlew :feature:devices:build

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :core:data:test

# Check code formatting (doesn't modify files)
./gradlew lintKotlin

# Format all Kotlin files
./gradlew formatKotlin

# Clean build
./gradlew clean build

# Assemble debug APK
./gradlew assembleDebug

# Run app on connected device
./gradlew installDebug

# Check project structure
./gradlew projects

# See module dependencies
./gradlew :app:dependencies
```

## Future Considerations

1. **Domain Layer**: Consider adding use cases in a `:core:domain` module for complex business logic
2. **Offline-First**: Enhance repository with sync strategies and offline support
3. **UI State Modeling**: Standardize UI state with sealed hierarchies across features
4. **Screenshot Testing**: Add Roborazzi for visual regression testing
5. **Baseline Profiles**: Add baseline profiles for improved app startup performance
6. **Analytics Module**: Create `:core:analytics` for event tracking
7. **Testing Infrastructure**: Expand test doubles (fakes) for better testability
8. **Performance Monitoring**: Add database operation metrics and Compose performance tracking

## Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Room Relationships](https://developer.android.com/training/data-storage/room/relationships)
- [Room Migrations](https://developer.android.com/training/data-storage/room/migrating-db-versions)
- [Circuit Architecture](https://slackhq.github.io/circuit/)
- [Circuit Testing Guide](https://slackhq.github.io/circuit/testing/)
- [Timber Logging](https://github.com/JakeWharton/timber)
- [Metro Dependency Injection](https://zacsweers.github.io/metro/)
- [Metro Multi-Module Setup](https://zacsweers.github.io/metro/multibindings/)
- [CatchUp App](https://github.com/ZacSweers/CatchUp) - Reference implementation for Metro multi-module DI
- [Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design System](https://m3.material.io/)
- [Material 3 Compose Components](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Android Dynamic Color Guide](https://developer.android.com/develop/ui/views/theming/dynamic-colors)
- [Android Test Doubles Guide](https://developer.android.com/training/testing/fundamentals/test-doubles)
- [Now in Android Repository](https://github.com/android/nowinandroid) - Reference for best practices
- [Modularization Learning Journey](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
- [Architecture Learning Journey](https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md)

## Notes for AI Assistants

- Always suggest running `formatKotlin` and `test` before commits
- **Always update CHANGELOG.md** following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format
- Add changes to `[Unreleased]` section with appropriate category (Added, Changed, Deprecated, Removed, Fixed, Security)
- **CRITICAL: Avoid duplicate section headers in CHANGELOG.md**
  - Before adding a new section header (e.g., `### Added`, `### Changed`, `### Fixed`), **always check if that section already exists** in the `[Unreleased]` section
  - If the section header already exists, **add your entry to the existing section** rather than creating a duplicate header
  - Only create a new section header if it doesn't already exist in `[Unreleased]`
- Use [Semantic Versioning](https://semver.org/) for version numbers (MAJOR.MINOR.PATCH)
- Use Metro DI with constructor injection (via `@Inject` constructor parameters)
- **Import DI infrastructure from `:core:di`** module (`ActivityKey`, `WorkerKey`, etc.)
- Use `@ContributesTo` pattern for multibindings in shared modules
- Write comprehensive unit tests for new features using JUnit assertions
- Follow the **multi-module architecture**: Create feature modules under `feature/`, use core modules for shared code
- Use **convention plugins** from `build-logic` for consistent build configuration
- Always use **`:core:designsystem`** components instead of creating custom UI components
- Place shared utilities in appropriate core modules (`core:common`, `core:model`, etc.)
- Feature modules should use the Circuit Screen/Presenter/UI pattern
- Source files use **`kotlin/`** directory (not `java/`) - follow existing module structure
- Don't use PII (Personally Identifiable Information) in code examples or tests
- **Do NOT create summary markdown files** (like `FEATURE_SUMMARY.md`, `SCREENS_IMPLEMENTATION.md`, etc.) for features or bug fixes
- Keep documentation in existing files like README.md, CHANGELOG.md, or inline code comments
- The app uses Room database with proper entity relationships and auto-migrations in `:core:database` module

### GitHub Operations

**ALWAYS use GitHub MCP tools for GitHub operations:**

- **Creating Pull Requests**: Use `mcp_github_create_pull_request` tool
  - Never use `gh pr create` CLI command
  - Provide comprehensive PR description with changes, testing, and checklist
  - Include code examples and screenshots when relevant

- **Reading Pull Requests**: Use `mcp_github_pull_request_read` tool
  - Get PR details, diff, status, files, reviews, or comments
  - Never use `gh pr view` or `gh pr diff` CLI commands

- **Reading Issues**: Use `mcp_github_get_issue` tool
  - Never use `gh issue view` CLI command

- **Creating Issues**: Use `mcp_github_create_issue` tool
  - Never use `gh issue create` CLI command

- **Searching**: Use MCP search tools for code, issues, PRs, or repositories
  - `mcp_github_search_code` for code search
  - `mcp_github_search_issues` for issue search
  - `mcp_github_search_pull_requests` for PR search
  - Never use `gh search` CLI commands

**If GitHub MCP tools are not available:**
- **DO NOT** fall back to `gh` CLI commands
- Instead, inform the user that GitHub MCP tools are required
- Ask the user to install the GitHub MCP server
- Provide setup instructions if needed

**Rationale:**
- MCP tools provide better structured responses
- Consistent API-based interaction
- Better error handling and type safety
- Avoid shell command parsing issues
