package ir.arminniromandi.arminshop.data.repository

import android.util.Log
import ir.arminniromandi.arminshop.data.local.dao.CartDao
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.data.local.security.SecureTokenManager
import ir.arminniromandi.arminshop.data.remote.ShopApiService
import ir.arminniromandi.arminshop.data.remote.dto.AddToCartRequest
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val shopApiService: ShopApiService,
    private val secureTokenManager: SecureTokenManager
) : CartRepository {

    companion object {
        private const val TAG = "CartRepositoryImpl"
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            syncCartFromRemote()
        }
    }

    private suspend fun syncCartFromRemote() {
        if (!secureTokenManager.isLoggedIn()) return
        try {
            val response = shopApiService.getCart()
            if (response.isSuccessful) {
                val remoteItems = response.body()?.items ?: emptyList()
                for (item in remoteItems) {
                    val p = item.product
                    if (p != null) {
                        val cartItem = CartItemEntity(
                            productId = item.productId,
                            name = p.name,
                            imageRes = ir.arminniromandi.arminshop.R.drawable.man_category,
                            imageUrl = p.imageUrl,
                            price = p.price,
                            originalPrice = p.price,
                            quantity = item.quantity
                        )
                        cartDao.insertOrUpdate(cartItem)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline or server unreachable for cart sync: ${e.message}")
        }
    }

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
                imageUrl = product.imageUrl,
                price = price,
                originalPrice = originalPrice,
                quantity = 1
            )
            cartDao.insertOrUpdate(newItem)
        }

        // Async sync to server if authenticated
        if (secureTokenManager.isLoggedIn()) {
            repositoryScope.launch {
                try {
                    shopApiService.addToCart(AddToCartRequest(productId = product.id, quantity = 1))
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to sync cart add to server: ${e.message}")
                }
            }
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
