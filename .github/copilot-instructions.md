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

#### Formatting
We use ktlint for code formatting. Ensure your code adheres to the project's style guide by running `./gradlew formatKotlin` before committing changes.

### Performance Guidelines

1. **Use paging** for large datasets
2. **Add indexes** on frequently queried columns
3. **Use transactions** for bulk operations
4. **Leverage relationship queries** instead of multiple database calls

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
- [Timber Logging](https://github.com/JakeWharton/timber)
- [Metro Dependency Injection](https://zacsweers.github.io/metro/)
