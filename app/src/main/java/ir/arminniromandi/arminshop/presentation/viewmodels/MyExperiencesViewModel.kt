package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.OrderRepository
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MyExperiencesViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val orders: StateFlow<List<OrderEntity>> = orderRepository.getAllOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun submitReview(
        orderId: Long,
        productId: Int,
        rating: Float,
        commentText: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = userRepository.getUserData().firstOrNull()
            val userName = user?.fullName?.ifBlank { "کاربر" } ?: "کاربر"

            val newComment = ProductComment(
                id = Random.nextInt(10000, 99999),
                userName = userName,
                rating = rating,
                text = commentText.trim(),
                date = "۲۶ مهر ۱۴۰۲"
            )

            // 1. Add to comments in database and update product average rating
            productRepository.addComment(productId, newComment)

            // 2. Update order record with comment flag, rating, and text
            orderRepository.updateOrderComment(orderId, rating, commentText.trim())

            onSuccess()
        }
    }
}
