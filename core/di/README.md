# Core DI Module

This module provides shared dependency injection infrastructure for the device catalog app using [Metro](https://zacsweers.github.io/metro/).

## Purpose

The `:core:di` module centralizes DI annotations and re-exports `dev.zacsweers.metro:metrox-android` for Android app component injection (`MetroAppComponentFactory`, `MetroAppComponentProviders`, `MetroApplication`, `@ActivityKey`, etc.).

## Components

### `metrox-android` Re-exports

- `ActivityKey` (`dev.zacsweers.metrox.android.ActivityKey`): Uses `@MapKey(implicitClassKey = true)` to bind Activities into `MetroAppComponentProviders.activityProviders`.
- `MetroAppComponentFactory` & `MetroAppComponentProviders`: Constructor-injects `Activity`, `Service`, `BroadcastReceiver`, and `ContentProvider` classes on API 28+.

```kotlin
@ActivityKey
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
@Inject
class MainActivity(private val circuit: Circuit) : ComponentActivity()
```

### `WorkerKey`

Map key for binding WorkManager workers via `AppWorkerFactory`.

```kotlin
@WorkerKey(DeviceSyncWorker::class)
@ContributesIntoMap(AppScope::class, binding = binding<WorkerInstanceFactory<*>>())
@AssistedFactory
abstract class Factory : WorkerInstanceFactory<DeviceSyncWorker>
```

## References

- [Metro Documentation](https://zacsweers.github.io/metro/)
- [Metro Multibindings](https://zacsweers.github.io/metro/multibindings/)
- [Metro Circuit Integration](https://zacsweers.github.io/metro/latest/circuit/)
