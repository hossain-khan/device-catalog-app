# Core DI Module

This module provides shared dependency injection infrastructure for the device catalog app using [Metro](https://zacsweers.github.io/metro/).

## Purpose

The `:core:di` module centralizes DI annotations and utilities that are used across multiple modules, following the multi-module DI pattern from [CatchUp](https://github.com/ZacSweers/CatchUp).

## Components

### MapKey Annotations

#### `ActivityKey`
Type-safe map key for binding Activities in the dependency graph.

```kotlin
@ActivityKey(MainActivity::class)
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
@Inject
class MainActivity(private val circuit: Circuit) : ComponentActivity()
```

#### `WorkerKey`
Map key for binding WorkManager workers.

```kotlin
@WorkerKey(DeviceSyncWorker::class)
@ContributesIntoMap(AppScope::class, binding = binding<WorkerInstanceFactory<*>>())
@AssistedFactory
abstract class Factory : WorkerInstanceFactory<DeviceSyncWorker>
```

### Multibinding Interfaces

#### `UiMultibindings`
Declares multibindings for UI components. Uses `@ContributesTo(AppScope::class)` so it's automatically discovered by Metro.

```kotlin
@ContributesTo(AppScope::class)
interface UiMultibindings {
    @Multibinds
    fun activityProviders(): Map<KClass<out Activity>, Provider<Activity>>
}
```

## Usage

### In App Module

The app module includes this module and uses the defined keys:

```kotlin
dependencies {
    implementation(project(":core:di"))
}
```

### In Feature Modules

Feature modules can import DI infrastructure as needed:

```kotlin
import dev.hossain.devicecatalog.core.di.ActivityKey
import dev.hossain.devicecatalog.core.di.WorkerKey
```

## Best Practices

1. **Centralized DI Infrastructure**: All DI-related keys and multibinding interfaces live here
2. **@ContributesTo Pattern**: Use for automatic discovery by Metro
3. **Type Safety**: MapKey annotations provide compile-time type safety
4. **Multi-Module Support**: Enables feature modules to contribute bindings independently

## References

- [Metro Documentation](https://zacsweers.github.io/metro/)
- [Metro Multibindings](https://zacsweers.github.io/metro/multibindings/)
- [CatchUp DI Setup](https://github.com/ZacSweers/CatchUp/tree/main/libraries/di)
