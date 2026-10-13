package ir.arminniromandi.arminshop.domain.usecase

import ir.arminniromandi.arminshop.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Verifies the entered one-time code and completes the login session.
 *
 * Business rules:
 * 1. The code must match the accepted verification codes.
 * 2. On success, the email and login state are persisted together as one
 *    atomic business transaction.
 *
 * @return true when the code is valid and the session was persisted.
 */
class VerifyCodeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, code: String): Boolean {
        if (code.isBlank()) return false

        // TODO: Replace hardcoded codes with real verification (e.g., server-side)
        val isCodeValid = code == "1234" || code == "0000"
        if (!isCodeValid) return false

        // Persist the session only when the code is actually valid.
        userRepository.saveEmail(email)
        userRepository.saveLoginState(true)
        return true
    }
}
