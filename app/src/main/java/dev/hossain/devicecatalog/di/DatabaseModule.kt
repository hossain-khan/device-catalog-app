package dev.hossain.devicecatalog.di

import android.content.Context
import androidx.room.Room
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Module
import dagger.Provides
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AppDatabase
import kotlin.jvm.java

@Module
@ContributesTo(AppScope::class)
object DatabaseModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "device_catalog.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAndroidDeviceDao(database: AppDatabase): AndroidDeviceDao = database.androidDeviceDao()
}
