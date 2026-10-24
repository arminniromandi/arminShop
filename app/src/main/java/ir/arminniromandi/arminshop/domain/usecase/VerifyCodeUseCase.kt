package ir.arminniromandi.arminshop.domain.usecase

import ir.arminniromandi.arminshop.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Verifies the entered OTP code with the backend API and persists the secure session.
 */
class VerifyCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, code: String): Boolean {
        if (code.isBlank()) return false
        val result = userRepository.verifyOtp(email, code)
        return result.isSuccess && result.getOrDefault(false)
    }
}
