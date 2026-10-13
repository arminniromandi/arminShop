package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(productId: Int): Flow<Product?>
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun addComment(productId: Int, comment: ProductComment)
}
