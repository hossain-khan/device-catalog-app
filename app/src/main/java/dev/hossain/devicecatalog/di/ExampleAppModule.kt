package dev.hossain.devicecatalog.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.hossain.devicecatalog.data.ExampleEmailValidator

// Example of a Dagger module that provides dependencies for the app.
// You should delete this file and create your own modules.
@ContributesTo(AppScope::class)
@Module
class ExampleAppModule {
    @Provides
    fun provideEmailRepository(): ExampleEmailValidator = ExampleEmailValidator()
}
