package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.arminniromandi.arminshop.presentation.ui.screens.LoginStep
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * UI State for the Login screen
 */
data class LoginUiState(
    val currentStep: LoginStep = LoginStep.EMAIL,
    val email: String = "",
    val verificationCode: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false,
    val isCodeError: Boolean = false,
    val timerSeconds: Int = 201
) {
    val isEmailSubmitEnabled: Boolean
        get() = email.isNotBlank()

    val isCodeSubmitEnabled: Boolean
        get() = verificationCode.isNotBlank()

    val isUserInfoSubmitEnabled: Boolean
        get() = fullName.isNotBlank()

    val formattedTimer: String
        get() = formatTimer(timerSeconds)
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onVerificationCodeChange(newCode: String) {
        _uiState.update {
            it.copy(
                verificationCode = newCode,
                isCodeError = false
            )
        }
    }

    fun onFullNameChange(newName: String) {
        _uiState.update { it.copy(fullName = newName) }
    }

    fun submitEmail() {
        val currentEmail = _uiState.value.email
        if (currentEmail.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1200L)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentStep = LoginStep.VERIFICATION_CODE,
                    timerSeconds = 201,
                    isCodeError = false
                )
            }
            startTimer()
        }
    }

    fun submitVerificationCode() {
        val code = _uiState.value.verificationCode
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500L)
            if ((code == "1234") || (code == "0000")) {
                stopTimer()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStep = LoginStep.USER_INFO,
                        isCodeError = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isCodeError = true
                    )
                }
            }
        }
    }

    fun submitUserInfo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000L)
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }

    fun editEmail() {
        stopTimer()
        _uiState.update {
            it.copy(
                currentStep = LoginStep.EMAIL,
                verificationCode = "",
                isCodeError = false
            )
        }
    }

    fun resendCode() {
        _uiState.update {
            it.copy(
                timerSeconds = 201,
                verificationCode = "",
                isCodeError = false
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000L)
                _uiState.update {
                    if (it.timerSeconds > 0) it.copy(timerSeconds = it.timerSeconds - 1) else it
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

/**
 * Formats countdown seconds to MM:SS string with Persian digits
 */
private fun formatTimer(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val rawString = String.format(Locale.US, "%02d:%02d", minutes, seconds)
    return rawString.toPersianDigits()
}

/**
 * Converts Western digits to Persian digits
 */
private fun String.toPersianDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}
