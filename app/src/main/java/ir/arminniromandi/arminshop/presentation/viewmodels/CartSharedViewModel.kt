package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.UserAddress
import ir.arminniromandi.arminshop.domain.repository.AddressRepository
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import ir.arminniromandi.arminshop.domain.repository.NotificationRepository
import ir.arminniromandi.arminshop.domain.repository.OrderRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class OrderSuccessInfo(
    val orderTrackingCode: String,
    val totalAmount: Long,
    val itemsCount: Int,
    val recipientName: String,
    val address: String,
    val deliveryEstimate: String = "تقریباً تا ۵ روز آینده"
)

@HiltViewModel
class CartSharedViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository,
    private val notificationRepository: NotificationRepository,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    val shippingFee: Long = 100_000L

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

    // Subtotal before any discount
    val subtotalPrice: StateFlow<Long> = cartItems.map { items ->
        items.sumOf { (if (it.originalPrice > 0) it.originalPrice else it.price) * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Total discount amount
    val discountAmount: StateFlow<Long> = cartItems.map { items ->
        items.sumOf { item ->
            val orig = if (item.originalPrice > 0) item.originalPrice else item.price
            (orig - item.price).coerceAtLeast(0L) * item.quantity
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Net items price (subtotal - discount)
    val itemsFinalPrice: StateFlow<Long> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Grand total including shipping fee
    val grandTotal: StateFlow<Long> = itemsFinalPrice.map { itemsTotal ->
        if (itemsTotal > 0) itemsTotal + shippingFee else 0L
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Default address from database
    val defaultAddress: StateFlow<UserAddress?> = addressRepository.getDefaultAddress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Selected address for the current checkout
    private val _selectedAddress = MutableStateFlow<UserAddress?>(null)
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()

    // Order Success State
    private val _orderSuccessInfo = MutableStateFlow<OrderSuccessInfo?>(null)
    val orderSuccessInfo: StateFlow<OrderSuccessInfo?> = _orderSuccessInfo.asStateFlow()

    private val _topNotificationMessage = MutableStateFlow<String?>(null)
    val topNotificationMessage: StateFlow<String?> = _topNotificationMessage.asStateFlow()

    private var notificationDismissJob: Job? = null

    fun selectAddress(address: UserAddress) {
        _selectedAddress.value = address
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product)
            showNotification("۱ آیتم به سبد خرید اضافه شد")
            toastNotifier.showToast("«${product.name}» به سبد خرید افزوده شد")
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
            toastNotifier.showToast("کالا از سبد خرید حذف شد")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
            toastNotifier.showToast("سبد خرید خالی شد")
        }
    }

    /**
     * Completes checkout:
     * 1. Saves purchased items into orders table in Room database
     * 2. Adds notification into notifications table in Room database
     * 3. Clears cart in Room database and posts payment success.
     */
    fun completePayment(deliveryAddress: UserAddress) {
        val currentItems = cartItems.value
        if (currentItems.isEmpty()) {
            toastNotifier.showError("سبد خرید شما خالی است")
            return
        }

        val total = grandTotal.value
        val count = cartTotalCount.value
        val rawCode = Random.nextInt(10000, 99999).toString()
        val trackingCode = "#SH-$rawCode"

        viewModelScope.launch {
            try {
                // Save each item as an order entity
                val orderEntities = currentItems.map { item ->
                    OrderEntity(
                        orderCode = rawCode,
                        orderDate = "۲۶ مهر ۱۴۰۲",
                        productId = item.productId,
                        productName = item.name,
                        productImageRes = item.imageRes,
                        productImageUrl = item.imageUrl,
                        price = item.price,
                        quantity = item.quantity,
                        isPaymentSuccess = true,
                        hasCommented = false
                    )
                }
                orderRepository.addOrders(orderEntities)

                // Save order registration notification into database
                val notification = NotificationEntity(
                    title = "ثبت موفق سفارش",
                    sender = "فروشگاه",
                    date = "۲۶ مهر ۱۴۰۲",
                    message = "سفارش شما با کد پیگیری $rawCode با موفقیت در فروشگاه ثبت شد و در حال آماده‌سازی می‌باشد.",
                    isRead = false,
                    orderCode = rawCode
                )
                notificationRepository.addNotification(notification)

                // Clear the cart
                cartRepository.clearCart()

                _orderSuccessInfo.value = OrderSuccessInfo(
                    orderTrackingCode = trackingCode,
                    totalAmount = total,
                    itemsCount = count,
                    recipientName = deliveryAddress.recipientName,
                    address = deliveryAddress.fullAddress
                )
                toastNotifier.showSuccess("پرداخت با موفقیت انجام شد و سفارش ثبت گردید")
            } catch (e: Exception) {
                toastNotifier.showError("خطا در ثبت سفارش: ${e.message}")
            }
        }
    }

    fun dismissOrderSuccess() {
        _orderSuccessInfo.value = null
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
