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
    val phoneNumber: String?,
    val birthDate: String?,
    val gender: String?,
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
            phoneNumber = preferences[PreferencesKeys.PHONE_NUMBER],
            birthDate = preferences[PreferencesKeys.BIRTH_DATE],
            gender = preferences[PreferencesKeys.GENDER],
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

    suspend fun saveUserProfile(
        fullName: String,
        phoneNumber: String,
        email: String,
        birthDate: String,
        gender: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FULL_NAME] = fullName
            preferences[PreferencesKeys.PHONE_NUMBER] = phoneNumber
            preferences[PreferencesKeys.EMAIL] = email
            preferences[PreferencesKeys.BIRTH_DATE] = birthDate
            preferences[PreferencesKeys.GENDER] = gender
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
        val PHONE_NUMBER = stringPreferencesKey("user_phone_number")
        val BIRTH_DATE = stringPreferencesKey("user_birth_date")
        val GENDER = stringPreferencesKey("user_gender")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
}
