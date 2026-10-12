package ir.arminniromandi.arminshop.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.arminniromandi.arminshop.data.local.UserPreferencesDataSource
import ir.arminniromandi.arminshop.data.repository.UserRepositoryImpl
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(userPreferencesDataSource: UserPreferencesDataSource): UserRepository {
        return UserRepositoryImpl(userPreferencesDataSource)
    }
}
