package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartSharedViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItemEntity>> = cartRepository.getCartItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cartTotalCount: StateFlow<Int> = cartRepository.getCartTotalCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _topNotificationMessage = MutableStateFlow<String?>(null)
    val topNotificationMessage: StateFlow<String?> = _topNotificationMessage.asStateFlow()

    private var notificationDismissJob: Job? = null

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product)
            showNotification("۱ آیتم به سبد خرید اضافه شد")
        }
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun dismissNotification() {
        notificationDismissJob?.cancel()
        _topNotificationMessage.value = null
    }

    private fun showNotification(message: String) {
        notificationDismissJob?.cancel()
        _topNotificationMessage.value = message
        notificationDismissJob = viewModelScope.launch {
            delay(2500)
            _topNotificationMessage.value = null
        }
    }
}
