package ir.arminniromandi.arminshop.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserPreferencesData(
    val email: String?,
    val fullName: String?,
    val isLoggedIn: Boolean
)

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val userPreferencesData: Flow<UserPreferencesData> = dataStore.data.map { preferences ->
        UserPreferencesData(
            email = preferences[PreferencesKeys.EMAIL],
            fullName = preferences[PreferencesKeys.FULL_NAME],
            isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        )
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL] = email
        }
    }

    suspend fun saveFullName(fullName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FULL_NAME] = fullName
        }
    }

    suspend fun saveUserData(email: String, fullName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL] = email
            preferences[PreferencesKeys.FULL_NAME] = fullName
        }
    }

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object PreferencesKeys {
        val EMAIL = stringPreferencesKey("user_email")
        val FULL_NAME = stringPreferencesKey("user_full_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
}
