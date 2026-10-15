package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItemEntity>>
    fun getCartTotalCount(): Flow<Int>
    suspend fun addToCart(product: Product)
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun removeFromCart(productId: Int)
    suspend fun clearCart()
}
