package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.UserPreferencesDataSource
import ir.arminniromandi.arminshop.domain.model.User
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserRepository {

    override fun getUserData(): Flow<User?> {
        return userPreferencesDataSource.userPreferencesData.map { preferencesData ->
            User(
                email = preferencesData.email ?: "",
                fullName = preferencesData.fullName ?: "",
                phoneNumber = preferencesData.phoneNumber ?: "",
                birthDate = preferencesData.birthDate ?: "",
                gender = preferencesData.gender ?: ""
            )
        }
    }

    override fun getLoginState(): Flow<Boolean> {
        return userPreferencesDataSource.isLoggedIn
    }

    override suspend fun saveUserData(email: String, fullName: String) {
        userPreferencesDataSource.saveUserData(email, fullName)
    }

    override suspend fun saveUserProfile(
        fullName: String,
        phoneNumber: String,
        email: String,
        birthDate: String,
        gender: String
    ) {
        userPreferencesDataSource.saveUserProfile(
            fullName = fullName,
            phoneNumber = phoneNumber,
            email = email,
            birthDate = birthDate,
            gender = gender
        )
    }

    override suspend fun saveEmail(email: String) {
        userPreferencesDataSource.saveEmail(email)
    }

    override suspend fun saveFullName(fullName: String) {
        userPreferencesDataSource.saveFullName(fullName)
    }

    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        userPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun clearUserData() {
        userPreferencesDataSource.clear()
    }
}
