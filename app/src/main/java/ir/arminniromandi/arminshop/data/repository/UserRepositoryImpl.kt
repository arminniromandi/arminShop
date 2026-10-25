package ir.arminniromandi.arminshop.data.repository

import android.util.Log
import ir.arminniromandi.arminshop.data.local.UserPreferencesDataSource
import ir.arminniromandi.arminshop.data.local.security.SecureTokenManager
import ir.arminniromandi.arminshop.data.remote.ShopApiService
import ir.arminniromandi.arminshop.data.remote.dto.SendOtpRequest
import ir.arminniromandi.arminshop.data.remote.dto.VerifyOtpRequest
import ir.arminniromandi.arminshop.domain.model.User
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val secureTokenManager: SecureTokenManager,
    private val shopApiService: ShopApiService,
    private val toastNotifier: ToastNotifier
) : UserRepository {

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }

    override fun getUserData(): Flow<User?> {
        return userPreferencesDataSource.userPreferencesData.map { preferencesData ->
            User(
                email = preferencesData.email ?: secureTokenManager.getEmail() ?: "",
                fullName = preferencesData.fullName ?: "",
                phoneNumber = preferencesData.phoneNumber ?: "",
                birthDate = preferencesData.birthDate ?: "",
                gender = preferencesData.gender ?: ""
            )
        }
    }

    override fun getLoginState(): Flow<Boolean> {
        return userPreferencesDataSource.isLoggedIn.map { isLoggedInPref ->
            isLoggedInPref || secureTokenManager.isLoggedIn()
        }
    }

    override suspend fun saveUserData(email: String, fullName: String) {
        secureTokenManager.saveEmail(email)
        userPreferencesDataSource.saveUserData(email, fullName)
    }

    override suspend fun saveUserProfile(
        fullName: String,
        phoneNumber: String,
        email: String,
        birthDate: String,
        gender: String
    ) {
        secureTokenManager.saveEmail(email)
        userPreferencesDataSource.saveUserProfile(
            fullName = fullName,
            phoneNumber = phoneNumber,
            email = email,
            birthDate = birthDate,
            gender = gender
        )
    }

    override suspend fun saveEmail(email: String) {
        secureTokenManager.saveEmail(email)
        userPreferencesDataSource.saveEmail(email)
    }

    override suspend fun saveFullName(fullName: String) {
        userPreferencesDataSource.saveFullName(fullName)
    }

    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        userPreferencesDataSource.saveLoginState(isLoggedIn)
        if (!isLoggedIn) {
            secureTokenManager.clearToken()
        }
    }

    override suspend fun clearUserData() {
        secureTokenManager.clearAll()
        userPreferencesDataSource.clear()
    }

    override fun getAuthToken(): String? {
        return secureTokenManager.getToken()
    }

    override suspend fun sendOtp(email: String): Result<String> {
        return try {
            val response = shopApiService.sendOtp(SendOtpRequest(email = email))
            if (response.isSuccessful) {
                saveEmail(email)
                val msg = response.body()?.message ?: "کد تأیید به ایمیل شما ارسال شد"
                toastNotifier.showSuccess(msg)
                Result.success(msg)
            } else {
                val rawError = response.errorBody()?.string()
                val parsedMessage = extractMessage(rawError) ?: "خطا در ارسال کد تأیید (کد ${response.code()})"
                Log.e(TAG, "sendOtp failed: $parsedMessage")
                toastNotifier.showServerError(response.code(), "ارسال کد تأیید", parsedMessage)
                Result.failure(Exception(parsedMessage))
            }
        } catch (e: Exception) {
            Log.w(TAG, "sendOtp network error: ${e.message}")
            val errorMsg = "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت را بررسی کنید."
            toastNotifier.showNetworkError("ارسال کد تأیید")
            Result.failure(Exception(errorMsg))
        }
    }

    override suspend fun verifyOtp(email: String, code: String): Result<Boolean> {
        return try {
            val response = shopApiService.verifyOtp(VerifyOtpRequest(email = email, code = code))
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                if (!token.isNullOrBlank()) {
                    secureTokenManager.saveToken(token)
                    secureTokenManager.saveEmail(email)
                    userPreferencesDataSource.saveEmail(email)
                    userPreferencesDataSource.saveLoginState(true)
                    Log.d(TAG, "Successfully authenticated with JWT token")
                    toastNotifier.showSuccess("ورود با موفقیت انجام شد")
                    Result.success(true)
                } else {
                    val errorMsg = "توکن معتبر از سرور دریافت نشد"
                    toastNotifier.showError(errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val rawError = response.errorBody()?.string()
                val parsed = extractMessage(rawError) ?: "کد تأیید وارد شده نامعتبر یا منقضی است"
                Log.e(TAG, "verifyOtp failed: $parsed (Code: ${response.code()})")
                toastNotifier.showServerError(response.code(), "تأیید کد", parsed)
                Result.failure(Exception(parsed))
            }
        } catch (e: Exception) {
            Log.w(TAG, "verifyOtp network error: ${e.message}")
            val errorMsg = "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
            toastNotifier.showNetworkError("تأیید کد")
            Result.failure(Exception(errorMsg))
        }
    }

    private fun extractMessage(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            val json = JSONObject(raw)
            when {
                json.has("message") -> json.getString("message")
                json.has("error") -> json.getString("error")
                else -> null
            }
        } catch (_: Exception) {
            raw.takeIf { !it.contains("<html", ignoreCase = true) }
        }
    }
}
