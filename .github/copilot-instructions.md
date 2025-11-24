# Copilot Instructions - Android Device Catalog App

## Project Overview

This is an Android application that provides a catalog of Android devices with detailed specifications. The app uses a hybrid approach combining SQLDelight for schema definition and Room for runtime database operations with proper one-to-many relationships.
Check the [PRD](../project-resources/PRD.md) document for detailed requirements and features.

## Architecture & Tech Stack

### Core Technologies
- **Language**: Kotlin with Jetpack Compose
- **Database**: Room + SQLDelight (hybrid approach)
- **Dependency Injection**: Metro
- **UI Architecture**: Circuit (Compose-driven architecture)
- **Build System**: Gradle with Version Catalog
- **Logging**: Timber

### Key Libraries
- **Room Database**: For runtime database operations and relationships
- **SQLDelight**: For schema definition and database generation
- **Jetpack Compose**: Modern Android UI toolkit
- **Metro**: Dependency injection framework
- **Paging 3**: For efficient list loading
- **Circuit**: Slack's Compose-driven architecture
- **Timber**: Logging framework
- **Work Manager**: Background processing

## Database Architecture

### Hybrid SQLDelight + Room Approach

The project uses a unique hybrid approach:

1. **SQLDelight** (`Device.sq`) defines the database schema and generates the actual database file
2. **Room** provides runtime access with proper entity relationships and type safety

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

### Database Layer (`/db/`)
- `AppDatabase.kt` - Room database configuration
- `AndroidDeviceDao.kt` - Data access operations with relationship queries
- `AndroidDeviceEntity.kt` - Main device entity
- `AndroidDeviceWithRelations.kt` - Relationship data class
- `DeviceAbi.kt`, `DeviceOpenGl.kt`, etc. - Related entities

NOTE: Database is already preloaded with the app.

### Data Layer (`/data/`)
- `AndroidDeviceRepository.kt` - Repository pattern with Timber logging

### Schema Definition
- `Device.sq` - SQLDelight schema defining tables and relationships

## Development Guidelines

### Database Operations

1. **Always use transactions** for multi-table operations
2. **Use relationship queries** instead of manual joins
3. **Log operations** with Timber for debugging
4. **Handle foreign key constraints** properly

### Adding New Features

1. **Schema Changes**: Update `Device.sq` first, then Room entities
2. **Repository Methods**: Add comprehensive logging with `Timber`
3. **Error Handling**: Always wrap database operations in try-catch
4. **Testing**: Use Room's testing utilities. Use fake over mock for testing.

#### Example template files
Some example files are here as reference implementations, for example:
* [ExampleEmailDetailsScreen.kt](../app/src/main/java/dev/hossain/devicecatalog/circuit/ExampleEmailDetailsScreen.kt) is a circuit screen
* [ExampleInboxScreen.kt](../app/src/main/java/dev/hossain/devicecatalog/circuit/ExampleInboxScreen.kt) another circuit screen that shows navigation between screens
* [AppInfoOverlay.kt](../app/src/main/java/dev/hossain/devicecatalog/circuit/overlay/AppInfoOverlay.kt) shows how to show bottomsheet using circuit
* [SampleWorker.kt](../app/src/main/java/dev/hossain/devicecatalog/work/SampleWorker.kt) shows how to use `WorkManager` worker for background tasks

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
- **Assertions**: Always use [AssertK](https://github.com/assertk-org/assertk) for all test assertions when it's available in the project
  - **NEVER use JUnit assertions** (`assertEquals`, `assertTrue`, `assertNotNull`, etc.) if AssertK is available
  - Use assertk's fluent API: `assertThat(actual).isEqualTo(expected)`
  - Common assertions: `isEqualTo()`, `isNotNull()`, `isTrue()`, `isFalse()`, `hasSize()`, `isEmpty()`, `isInstanceOf()`, `isCloseTo()`
  - Benefits: Kotlin-native, better error messages, type-safe, null-safe
  - Example:
    ```kotlin
    import assertk.assertThat
    import assertk.assertions.*
    
    @Test
    fun `test example`() {
        val result = someFunction()
        assertThat(result).isEqualTo("expected")
        assertThat(result).hasLength(8)
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

1. **Room Testing**: Use `@Database(exportSchema = true)` for schema validation
2. **In-Memory Database**: For unit tests
3. **Migration Testing**: Test schema changes thoroughly

## Build Configuration

### Version Catalog (`gradle/libs.versions.toml`)
All dependencies are managed through the version catalog. When adding new dependencies:

1. Add version to `[versions]` section
2. Add library to `[libraries]` section  
3. Reference in `build.gradle.kts` as `libs.libraryName`

### Key Gradle Plugins
- `androidx.room` - Room compiler and schema export
- `ksp` - Kotlin Symbol Processing for Room
- `kotlin.compose` - Compose compiler

## Common Patterns

### Database Initialization
```kotlin
// Repository usage
val devices = repository.getAllDevices().collect { deviceList ->
    // Handle device list with all relationships loaded
}
```

### Inserting with Relationships
```kotlin
val device = AndroidDevice(/* device data */)
val deviceId = repository.insertDevice(device) // Handles all relationships
```

### Paging with Relationships
```kotlin
val pagedDevices = repository.getPagedDevices()
// Returns PagingData<AndroidDeviceWithRelations>
```

## Troubleshooting

### Schema Validation Errors
1. Ensure SQLDelight schema matches Room entities exactly
2. Check primary key definitions
3. Verify foreign key relationships
4. Regenerate database from updated SQLDelight schema

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
- Multiple database libraries (stick to Room + SQLDelight hybrid)

## Development Workflow

### Before Committing

**IMPORTANT**: Always run these commands before making a commit:

```bash
# 1. Format Kotlin code (auto-fixes style issues)
./gradlew formatKotlin

# 2. Run all tests (ensures nothing is broken)
./gradlew test

# 3. Run debug build to ensure no build issues
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
# 5. Commit with descriptive message
git commit -m "Add feature X

- Updated CHANGELOG.md with new feature"
```

### Common Gradle Tasks

```bash
# Build the project
./gradlew build

# Run specific module tests
./gradlew :app:test

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
```

## Future Considerations

1. **Database Migrations**: Plan for schema evolution with Room migrations
2. **Testing Strategy**: Expand test coverage for relationship queries
3. **Performance Monitoring**: Add database operation metrics
4. **Caching Strategy**: Consider adding caching layer if needed

## Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Room Relationships](https://developer.android.com/training/data-storage/room/relationships)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [Circuit Architecture](https://slackhq.github.io/circuit/)
- [Circuit Testing Guide](https://slackhq.github.io/circuit/testing/)
- [Timber Logging](https://github.com/JakeWharton/timber)
- [Metro Dependency Injection](https://zacsweers.github.io/metro/)
- [Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design System](https://m3.material.io/)
- [Material 3 Compose Components](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Android Dynamic Color Guide](https://developer.android.com/develop/ui/views/theming/dynamic-colors)
- [Android Test Doubles Guide](https://developer.android.com/training/testing/fundamentals/test-doubles)

## Notes for AI Assistants

- Always suggest running `formatKotlin` and `test` before commits
- **Always update CHANGELOG.md** following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format
- Add changes to `[Unreleased]` section with appropriate category (Added, Changed, Deprecated, Removed, Fixed, Security)
- **CRITICAL: Avoid duplicate section headers in CHANGELOG.md**
  - Before adding a new section header (e.g., `### Added`, `### Changed`, `### Fixed`), **always check if that section already exists** in the `[Unreleased]` section
  - If the section header already exists, **add your entry to the existing section** rather than creating a duplicate header
  - Only create a new section header if it doesn't already exist in `[Unreleased]`
- Use [Semantic Versioning](https://semver.org/) for version numbers (MAJOR.MINOR.PATCH)
- Prefer constructor injection over field injection
- Write comprehensive unit tests for new features
- Follow the existing code structure and patterns
- Don't use PII (Personally Identifiable Information) in code examples or tests
- **Do NOT create summary markdown files** (like `FEATURE_SUMMARY.md`, `SCREENS_IMPLEMENTATION.md`, etc.) for features or bug fixes
- Keep documentation in existing files like README.md, CHANGELOG.md, or inline code comments

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
