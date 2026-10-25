package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.domain.repository.AddressRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    val addresses: StateFlow<List<UserAddress>> = addressRepository.getAllAddresses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val defaultAddress: StateFlow<UserAddress?> = addressRepository.getDefaultAddress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _selectedAddress = MutableStateFlow<UserAddress?>(null)
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    fun selectAddress(address: UserAddress) {
        _selectedAddress.value = address
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            try {
                addressRepository.setDefaultAddress(id)
                val updated = addressRepository.getAddressById(id)
                if (updated != null) {
                    _selectedAddress.value = updated
                }
                toastNotifier.showSuccess("آدرس پیش‌فرض بروزرسانی شد")
            } catch (e: Exception) {
                toastNotifier.showError("خطا در تنظیم آدرس پیش‌فرض: ${e.message}")
            }
        }
    }

    fun saveAddress(
        id: Long = 0,
        recipientName: String,
        fullAddress: String,
        postalCode: String,
        phoneNumber: String,
        isDefault: Boolean = false,
        onSuccess: (UserAddress) -> Unit = {}
    ) {
        if (recipientName.isBlank()) {
            emitError("لطفاً نام گیرنده را وارد کنید")
            return
        }
        if (fullAddress.isBlank()) {
            emitError("لطفاً آدرس پستی را وارد کنید")
            return
        }
        if (postalCode.isBlank() || postalCode.length < 10) {
            emitError("کد پستی باید حداقل ۱۰ رقم باشد")
            return
        }
        if (phoneNumber.isBlank() || phoneNumber.length < 10) {
            emitError("لطفاً شماره همراه معتبر وارد کنید")
            return
        }

        viewModelScope.launch {
            try {
                val userAddress = UserAddress(
                    id = id,
                    recipientName = recipientName.trim(),
                    fullAddress = fullAddress.trim(),
                    postalCode = postalCode.trim(),
                    phoneNumber = phoneNumber.trim(),
                    isDefault = isDefault
                )
                if (id == 0L) {
                    val newId = addressRepository.addAddress(userAddress)
                    val saved = userAddress.copy(id = newId)
                    _selectedAddress.value = saved
                    toastNotifier.showSuccess("آدرس جدید با موفقیت اضافه شد")
                    onSuccess(saved)
                } else {
                    addressRepository.updateAddress(userAddress)
                    _selectedAddress.value = userAddress
                    toastNotifier.showSuccess("آدرس با موفقیت ویرایش شد")
                    onSuccess(userAddress)
                }
            } catch (e: Exception) {
                emitError("خطا در ذخیره آدرس: ${e.message}")
            }
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            try {
                addressRepository.deleteAddress(id)
                if (_selectedAddress.value?.id == id) {
                    _selectedAddress.value = null
                }
                toastNotifier.showToast("آدرس حذف شد")
            } catch (e: Exception) {
                toastNotifier.showError("خطا در حذف آدرس: ${e.message}")
            }
        }
    }

    private fun emitError(msg: String) {
        toastNotifier.showError(msg)
        viewModelScope.launch {
            _errorMessage.emit(msg)
        }
    }
}
