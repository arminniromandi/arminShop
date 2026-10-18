package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserData(): Flow<User?>
    fun getLoginState(): Flow<Boolean>
    suspend fun saveUserData(email: String, fullName: String)
    suspend fun saveUserProfile(
        fullName: String,
        phoneNumber: String,
        email: String,
        birthDate: String,
        gender: String
    )
    suspend fun saveEmail(email: String)
    suspend fun saveFullName(fullName: String)
    suspend fun saveLoginState(isLoggedIn: Boolean)
    suspend fun clearUserData()
}
