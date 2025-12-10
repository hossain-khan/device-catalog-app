# Database Update Guide

This guide explains how to update the bundled database (`devices.db`) and ensure users get the updated data when they update the app.

## Overview

The app uses a pre-populated Room database bundled as an asset. When users update the app, they need to receive the latest device catalog data.

## Current Configuration

Location: `app/src/main/java/dev/hossain/devicecatalog/di/DatabaseBindings.kt`

```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "device_catalog.db")
    .fallbackToDestructiveMigration(dropAllTables = true)
    .createFromAsset("devices.db")
    .build()
```

- `createFromAsset("devices.db")` - Loads database from `app/src/main/assets/devices.db` on first install
- `fallbackToDestructiveMigration(dropAllTables = true)` - Recreates database if migration fails

## Update Process

### Step 1: Update the Database File

1. Generate or obtain the new `devices.db` file with updated device data
2. Replace the existing database file at: `app/src/main/assets/devices.db`
3. Verify the database file is valid and contains the expected data

### Step 2: Increment Database Version

Location: `core/database/src/main/kotlin/dev/hossain/devicecatalog/core/database/AppDatabase.kt`

```kotlin
@Database(
    entities = [
        AndroidDeviceEntity::class,
        DeviceAbi::class,
        DeviceOpenGl::class,
        DeviceScreenDensity::class,
        DeviceScreenSize::class,
        DeviceSdk::class,
    ],
    version = 3, // ← INCREMENT THIS NUMBER
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        // Add new auto-migration if needed
        // AutoMigration(from = 2, to = 3),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun androidDeviceDao(): AndroidDeviceDao
}
```

**Action**: Change `version = 2` to `version = 3` (or next number)

### Step 3: Handle Schema Changes (If Applicable)

#### Option A: No Schema Changes (Data Only)
If you're only updating data without changing table structure:

1. Increment version number only
2. Add AutoMigration for the version jump:
   ```kotlin
   autoMigrations = [
       AutoMigration(from = 1, to = 2),
       AutoMigration(from = 2, to = 3), // New
   ]
   ```
3. The destructive migration will recreate the database with new data

#### Option B: Schema Changed
If you modified table structure (added/removed columns, tables, etc.):

1. Increment version number
2. Update entity classes with new schema
3. Either:
   - Add AutoMigration if Room can handle it automatically
   - Create manual Migration if complex changes needed
4. Update schema export files in `core/database/schemas/`

### Step 4: Test the Update

#### Test Scenarios

1. **Fresh Install** (new user):
   ```bash
   ./gradlew installDebug
   # Verify devices load correctly
   ```

2. **App Update** (existing user):
   ```bash
   # Install old version first
   ./gradlew installDebug
   
   # Verify devices appear
   # Note device count and data
   
   # Update database asset and increment version
   # Install new version
   ./gradlew installDebug
   
   # Verify:
   # - Database was recreated
   # - New devices appear
   # - Device count reflects updated data
   ```

3. **Check Logs**:
   ```bash
   adb logcat | grep "Room"
   ```
   Look for migration messages or errors

### Step 5: Verify in Code

The app should work seamlessly, but you can verify:

```kotlin
// In your repository or DAO
suspend fun getDatabaseVersion(): Int {
    // Check if data is from new version
    return dao.getDeviceCount()
}
```

## What Happens on App Update

### User Experience Flow

1. **User has app version 1.6.0** (database version 2)
   - Database exists at: `/data/data/dev.hossain.devicecatalog/databases/device_catalog.db`
   - Contains old device catalog

2. **User updates to version 1.7.0** (database version 3)
   - App starts
   - Room detects version mismatch (2 → 3)
   - `fallbackToDestructiveMigration` triggers
   - Old database is **deleted**
   - New database is **created from asset** (`devices.db`)
   - User sees updated device catalog

### Technical Flow

```
App Launch
    ↓
Room checks database version
    ↓
Version mismatch? (2 ≠ 3)
    ↓ Yes
Destructive migration triggered
    ↓
Drop all tables
    ↓
Create from asset (devices.db)
    ↓
Database ready with new data
```

## Important Notes

### ⚠️ Data Loss Warning

Since we use `fallbackToDestructiveMigration(dropAllTables = true)`:
- **All existing database data is deleted** on version change
- **User data is not preserved** (e.g., bookmarks, favorites, search history)
- This is acceptable for a **read-only catalog app** where data comes from the bundled asset

If you need to preserve user data in the future:
- Store user-specific data in a separate table
- Create proper migrations that preserve those tables
- Or use a different storage mechanism (SharedPreferences, separate DB)

### File Locations

- **Database Asset**: `app/src/main/assets/devices.db`
- **Database Config**: `app/src/main/java/dev/hossain/devicecatalog/di/DatabaseBindings.kt`
- **Schema Definition**: `core/database/src/main/kotlin/dev/hossain/devicecatalog/core/database/AppDatabase.kt`
- **Schema Exports**: `core/database/schemas/dev.hossain.devicecatalog.db.AppDatabase/`

### Build Process

The bundled database is included in the APK:
```
APK Structure
├── assets/
│   └── devices.db          ← Bundled database
├── classes.dex
├── resources.arsc
└── ...
```

APK size will increase based on database file size.

## Troubleshooting

### Users Not Seeing Updated Data

**Check**:
1. Did you increment the database version number?
2. Is the new `devices.db` file in `app/src/main/assets/`?
3. Did you rebuild the app? (`./gradlew clean assembleDebug`)
4. Is the database file valid? (open with SQLite browser)

### Migration Errors

**Check logs**:
```bash
adb logcat | grep -E "Room|Migration|SQLite"
```

**Common issues**:
- Schema mismatch between code and database file
- Corrupted database file
- Missing auto-migration declaration

### Database File Too Large

If `devices.db` becomes too large:
- Consider compressing tables
- Remove unnecessary indexes from bundled DB (create at runtime)
- Use `VACUUM` to compact the database
- Consider on-demand data loading instead of bundling

## Checklist

Before releasing an update with new database:

- [ ] Updated `devices.db` file in `app/src/main/assets/`
- [ ] Incremented version number in `AppDatabase.kt`
- [ ] Added AutoMigration declaration (if needed)
- [ ] Tested fresh install
- [ ] Tested app update from previous version
- [ ] Verified device count and data accuracy
- [ ] Checked APK size increase is acceptable
- [ ] Updated CHANGELOG.md with database update notes
- [ ] No errors in logcat during migration

## Example: Version 2 → 3 Update

**Before** (`AppDatabase.kt`):
```kotlin
@Database(
    entities = [...],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
```

**After** (`AppDatabase.kt`):
```kotlin
@Database(
    entities = [...],
    version = 3, // Incremented
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3), // Added
    ],
)
```

**Result**: 
- Users updating from v2 → v3 get database recreated with new data
- Fresh installs use the new bundled database

## Alternative Approach: Server-Sync

For future consideration, if you need more dynamic updates without app releases:

1. Bundle minimal database for offline-first experience
2. Implement server API for device catalog
3. Sync new/updated devices from server on app launch
4. Update local database incrementally
5. Users get updates without app store update

This requires:
- Backend API development
- Network handling
- Sync logic
- More complex but more flexible

---

**Last Updated**: December 9, 2025
