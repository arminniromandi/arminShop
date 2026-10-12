package ir.arminniromandi.arminshop.domain.usecase

import android.util.Patterns
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isBlank()) return false
        val isPatternValid = Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        val endsWithGmail = trimmed.endsWith("@gmail.com", ignoreCase = true)
        return isPatternValid && endsWithGmail
    }
}
