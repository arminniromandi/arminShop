package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import ir.arminniromandi.arminshop.domain.usecase.ValidateEmailUseCase
import ir.arminniromandi.arminshop.domain.usecase.VerifyCodeUseCase
import ir.arminniromandi.arminshop.presentation.ui.screens.LoginStep
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

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
    val isEmailError: Boolean = false,
    val timerSeconds: Int = 201,
    val isLoggedIn: Boolean? = null,
    val statusMessage: String? = null
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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        viewModelScope.launch {
            userRepository.getLoginState().collect { isLoggedIn ->
                _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
            }
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                isEmailError = false
            )
        }
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
        val currentEmail = _uiState.value.email.trim()
        if (!validateEmailUseCase(currentEmail)) {
            _uiState.update { it.copy(isEmailError = true) }
            toastNotifier.showError("لطفاً یک نشانی ایمیل معتبر وارد کنید")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEmailError = false) }
            val result = userRepository.sendOtp(currentEmail)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentStep = LoginStep.VERIFICATION_CODE,
                        timerSeconds = 201,
                        isCodeError = false,
                        statusMessage = result.getOrNull()
                    )
                }
                startTimer()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEmailError = true
                    )
                }
            }
        }
    }

    fun submitVerificationCode() {
        val code = _uiState.value.verificationCode.trim()
        val currentEmail = _uiState.value.email.trim()
        if (code.isBlank()) {
            toastNotifier.showError("لطفاً کد تأیید را وارد کنید")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isSuccess = verifyCodeUseCase(email = currentEmail, code = code)
            if (isSuccess) {
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
        val currentName = _uiState.value.fullName.trim()
        if (currentName.isBlank()) {
            toastNotifier.showError("لطفاً نام و نام خانوادگی خود را وارد کنید")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            userRepository.saveFullName(currentName)
            userRepository.saveLoginState(true)

            _uiState.update { it.copy(isLoading = false) }
            toastNotifier.showSuccess("اطلاعات شما با موفقیت ثبت شد")
            onSuccess()
        }
    }

    fun editEmail() {
        stopTimer()
        _uiState.update {
            it.copy(
                currentStep = LoginStep.EMAIL,
                verificationCode = "",
                isCodeError = false,
                isEmailError = false
            )
        }
    }

    fun resendCode() {
        val currentEmail = _uiState.value.email.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = userRepository.sendOtp(currentEmail)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    timerSeconds = 201,
                    verificationCode = "",
                    isCodeError = false
                )
            }
            if (result.isSuccess) {
                startTimer()
            }
        }
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
