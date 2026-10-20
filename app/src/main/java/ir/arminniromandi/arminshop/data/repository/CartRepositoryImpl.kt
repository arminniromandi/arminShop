package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.CartDao
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItemEntity>> {
        return cartDao.getAllCartItems()
    }

    override fun getCartTotalCount(): Flow<Int> {
        return cartDao.getTotalItemCount().map { it ?: 0 }
    }

    override suspend fun addToCart(product: Product): Unit = withContext(Dispatchers.IO) {
        val existingItem = cartDao.getCartItemById(product.id)
        if (existingItem != null) {
            cartDao.updateQuantity(product.id, existingItem.quantity + 1)
        } else {
            val originalPrice = product.mainPrice
            val price = if (product.discountPrec > 0 && product.finalPrice > 0) {
                product.finalPrice
            } else {
                product.mainPrice
            }
            val newItem = CartItemEntity(
                productId = product.id,
                name = product.name,
                imageRes = product.imageRes,
                price = price,
                originalPrice = originalPrice,
                quantity = 1
            )
            cartDao.insertOrUpdate(newItem)
        }
    }

    override suspend fun updateQuantity(productId: Int, quantity: Int): Unit = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItemByProductId(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    override suspend fun removeFromCart(productId: Int): Unit = withContext(Dispatchers.IO) {
        cartDao.deleteCartItemByProductId(productId)
    }

    override suspend fun clearCart(): Unit = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }
}
