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
            if (preferencesData.email != null) {
                User(
                    email = preferencesData.email,
                    fullName = preferencesData.fullName ?: ""
                )
            } else {
                null
            }
        }
    }

    override fun getLoginState(): Flow<Boolean> {
        return userPreferencesDataSource.isLoggedIn
    }

    override suspend fun saveUserData(email: String, fullName: String) {
        userPreferencesDataSource.saveUserData(email, fullName)
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
