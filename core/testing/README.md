# Testing Module

This module provides test infrastructure and fakes for testing the Device Catalog app.

## Overview

Following the **Now in Android** testing strategy, this module provides **test doubles (fakes)** instead of mocks. Fakes provide more realistic test behavior and exercise more production code, leading to better test coverage.

## Components

### Test Rules

#### TestDispatcherRule
JUnit test rule for configuring the Main dispatcher for coroutine testing.

**Usage:**
```kotlin
class MyPresenterTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    
    @Test
    fun `test my feature`() = runTest {
        // Test code with proper coroutine dispatcher
    }
}
```

### Fakes

#### FakeAndroidDeviceDao
Fake implementation of `AndroidDeviceDao` that stores data in-memory using `MutableStateFlow`.

**Features:**
- In-memory data storage
- Full implementation of all DAO methods
- Support for relationships (abis, screen densities, etc.)
- Paging support with `FakePagingSource`
- Test utilities: `setDevices()`, `clear()`

**Usage:**
```kotlin
val fakeDao = FakeAndroidDeviceDao()
fakeDao.setDevices(TestDeviceFactory.createSampleDevicesWithRelations())

val devices = fakeDao.getAllDevicesWithRelations().first()
```

#### FakeAndroidDeviceRepository
Fake implementation of `AndroidDeviceRepository` that uses `FakeAndroidDeviceDao` internally.

**Features:**
- All repository methods implemented
- Realistic data flow behavior
- Statistics calculation
- Quiz-related queries
- Test utilities: `setDevices()`, `clear()`

**Usage:**
```kotlin
val fakeRepository = FakeAndroidDeviceRepository()
fakeRepository.setDevices(TestDeviceFactory.createSampleDevices())

val devices = fakeRepository.getAllDevices().first()
val stats = fakeRepository.getDeviceStats().first()
```

### Test Data Factory

#### TestDeviceFactory
Factory object for creating test device data with realistic values.

**Methods:**
- `createAndroidDevice()` - Create AndroidDevice with customizable properties
- `createDeviceInfo()` - Create DeviceInfo domain model
- `createDeviceEntity()` - Create database entity
- `createDeviceWithRelations()` - Create entity with all relationships
- `createSampleDevices()` - Get a list of pre-configured sample devices
- `createSampleDevicesWithRelations()` - Get sample devices with relationships

**Usage:**
```kotlin
// Create a custom device
val device = TestDeviceFactory.createAndroidDevice(
    manufacturer = "Google",
    modelName = "Pixel 6",
    ram = "8GB"
)

// Get sample devices for tests
val sampleDevices = TestDeviceFactory.createSampleDevices()
```

## Testing Strategy

### Why Fakes Over Mocks?

1. **More Realistic**: Fakes behave like real implementations
2. **Better Coverage**: Exercises production code paths
3. **Easier to Maintain**: No need to update mock expectations
4. **Flexible**: Can be configured for different test scenarios

### Test Structure

```kotlin
class MyFeatureTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    
    private lateinit var repository: FakeAndroidDeviceRepository
    
    @Before
    fun setup() {
        repository = FakeAndroidDeviceRepository()
    }
    
    @Test
    fun `feature should work correctly`() = runTest {
        // Given
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val result = repository.getAllDevices().first()
        
        // Then
        assertEquals(3, result.size)
    }
}
```

## Adding to Your Module

To use the testing module in your tests, add this dependency:

```kotlin
dependencies {
    testImplementation(project(":core:testing"))
}
```

## Known Limitations

### Java Version Compatibility
The `android-device-catalog-parser` library dependency is compiled with Java 21, while the main project uses Java 17. This causes test execution issues when using `FormFactor` enum in test code. 

**Workarounds:**
1. Use the database entity types (`AndroidDeviceEntity`, `AndroidDeviceWithRelations`) in tests instead of domain models
2. Create devices directly without using `FormFactor` default parameters
3. Use the pre-configured sample data from `TestDeviceFactory.createSampleDevices()`

The fakes themselves compile and work correctly. This limitation only affects writing additional tests that use the domain model types directly.

## References

- [Now in Android Testing Strategy](https://github.com/android/nowinandroid#testing)
- [Android Test Doubles](https://developer.android.com/training/testing/fundamentals/test-doubles)
- [Kotlin Coroutines Testing](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)

## Examples

The test files in this module demonstrate the fake implementations (though they currently have Java version compatibility issues in execution):
- `FakeAndroidDeviceDaoTest.kt` - Examples of testing with fake DAO
- `FakeAndroidDeviceRepositoryTest.kt` - Examples of testing with fake repository
