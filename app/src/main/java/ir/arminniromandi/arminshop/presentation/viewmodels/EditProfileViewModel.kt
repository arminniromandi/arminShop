package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _birthDate = MutableStateFlow("")
    val birthDate: StateFlow<String> = _birthDate.asStateFlow()

    private val _gender = MutableStateFlow("مرد")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getUserData().firstOrNull()
            if (user != null) {
                _fullName.value = user.fullName.ifBlank { "بهرام افشاری" }
                _phoneNumber.value = user.phoneNumber.ifBlank { "۰۹۱۷۵۶۴۲۳۴۵۶" }
                _email.value = user.email
                _birthDate.value = user.birthDate.filter { it.isDigit() }.take(8)
                _gender.value = user.gender.ifBlank { "مرد" }
            } else {
                _fullName.value = "بهرام افشاری"
                _phoneNumber.value = "۰۹۱۷۵۶۴۲۳۴۵۶"
            }
        }
    }

    fun onFullNameChange(value: String) { _fullName.value = value }
    fun onPhoneNumberChange(value: String) { _phoneNumber.value = value }
    fun onEmailChange(value: String) { _email.value = value }
    fun onBirthDateChange(value: String) { _birthDate.value = value }
    fun onGenderChange(value: String) { _gender.value = value }

    fun saveChanges() {
        val trimmedName = _fullName.value.trim()
        if (trimmedName.isBlank()) {
            toastNotifier.showError("لطفاً نام و نام خانوادگی خود را وارد کنید")
            return
        }

        val trimmedPhone = _phoneNumber.value.trim()
        if (trimmedPhone.isBlank() || trimmedPhone.length < 10) {
            toastNotifier.showError("لطفاً شماره تلفن همراه معتبر (حداقل ۱۰ رقم) وارد کنید")
            return
        }

        viewModelScope.launch {
            try {
                val rawDigits = _birthDate.value.filter { it.isDigit() }.take(8)
                val formattedBirthDate = if (rawDigits.length == 8) {
                    "${rawDigits.substring(0, 4)}/${rawDigits.substring(4, 6)}/${rawDigits.substring(6, 8)}"
                } else {
                    _birthDate.value
                }

                userRepository.saveUserProfile(
                    fullName = trimmedName,
                    phoneNumber = trimmedPhone,
                    email = _email.value.trim(),
                    birthDate = formattedBirthDate,
                    gender = _gender.value
                )
                toastNotifier.showSuccess("اطلاعات کاربری با موفقیت ذخیره شد")
                _isSaved.value = true
            } catch (e: Exception) {
                toastNotifier.showError("خطا در ذخیره اطلاعات: ${e.message}")
            }
        }
    }

    fun resetSavedState() {
        _isSaved.value = false
    }
}
