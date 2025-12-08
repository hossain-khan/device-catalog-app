# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ================================================================================================
# Android Device Universe - ProGuard Rules
# ================================================================================================

# Preserve line numbers for debugging stack traces in production
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ================================================================================================
# Room Database
# ================================================================================================
# Keep all Room entities and DAOs to ensure database operations work correctly
-keep class dev.hossain.devicecatalog.core.database.** { *; }
-keep interface dev.hossain.devicecatalog.core.database.** { *; }

# Keep Room annotations
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }

# ================================================================================================
# Metro Dependency Injection
# ================================================================================================
# Keep all classes and members annotated with Metro annotations
-keep @dev.zacsweers.metro.annotations.* class * { *; }
-keepclassmembers class * {
    @dev.zacsweers.metro.annotations.* *;
}

# Keep Inject constructors and members
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}

# Keep AssistedInject constructors
-keepclasseswithmembernames class * {
    @com.squareup.anvil.annotations.ContributesBinding <methods>;
}

# ================================================================================================
# Circuit (Slack's Compose Architecture)
# ================================================================================================
# Keep all Circuit screens and UI states
-keep class * implements com.slack.circuit.runtime.screen.Screen { *; }
-keep class * implements com.slack.circuit.runtime.CircuitUiState { *; }
-keep class * implements com.slack.circuit.runtime.CircuitUiEvent { *; }

# Keep Circuit annotations
-keep @com.slack.circuit.codegen.annotations.CircuitInject class * { *; }
-keepclassmembers class * {
    @com.slack.circuit.codegen.annotations.CircuitInject *;
}

# Keep Circuit presenters
-keep class * implements com.slack.circuit.runtime.presenter.Presenter { *; }

# ================================================================================================
# Kotlinx Serialization
# ================================================================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Serializers
-keep,includedescriptorclasses class dev.hossain.devicecatalog.**$$serializer { *; }

# Keep Companion objects with serializers
-keepclassmembers class dev.hossain.devicecatalog.** {
    *** Companion;
}

# Keep classes with KSerializer
-keepclasseswithmembers class dev.hossain.devicecatalog.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes
-keep @kotlinx.serialization.Serializable class * { *; }

# ================================================================================================
# Parcelize
# ================================================================================================
# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class * implements android.os.Parcelable {
    public <fields>;
}

# Keep classes annotated with @Parcelize
-keep @kotlinx.parcelize.Parcelize class * { *; }

# ================================================================================================
# Timber Logging
# ================================================================================================
-keep class timber.log.** { *; }
-dontwarn org.jetbrains.annotations.**

# Remove debug and verbose logging in release builds for performance
-assumenosideeffects class timber.log.Timber* {
    public static *** d(...);
    public static *** v(...);
}

# ================================================================================================
# Jetpack Compose
# ================================================================================================
# Keep Compose runtime classes
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Keep Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# ================================================================================================
# Coil Image Loading
# ================================================================================================
-keep class coil.** { *; }
-keep interface coil.** { *; }

# ================================================================================================
# AndroidX Core & Lifecycle
# ================================================================================================
-keep class androidx.lifecycle.** { *; }
-keep class androidx.core.** { *; }

# ================================================================================================
# WorkManager
# ================================================================================================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ================================================================================================
# Retrofit / OkHttp (if used for network calls)
# ================================================================================================
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ================================================================================================
# Kotlin Coroutines
# ================================================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ================================================================================================
# General Android & Kotlin
# ================================================================================================
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom view constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================================================================================
# Data Classes & Models
# ================================================================================================
# Keep data classes in model packages
-keep class dev.hossain.devicecatalog.core.model.** { *; }

# Keep data class copy() method
-keepclassmembers class * {
    public *** copy(...);
}

# ================================================================================================
# Security - EncryptedSharedPreferences
# ================================================================================================
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ================================================================================================
# End of ProGuard Rules
# ================================================================================================