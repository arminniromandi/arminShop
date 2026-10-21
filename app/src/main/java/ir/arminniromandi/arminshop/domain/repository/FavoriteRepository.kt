package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteProducts(): Flow<List<Product>>
    fun isFavorite(productId: Int): Flow<Boolean>
    fun getFavoriteProductIds(): Flow<List<Int>>
    suspend fun addToFavorites(productId: Int)
    suspend fun removeFromFavorites(productId: Int)
    suspend fun toggleFavorite(productId: Int)
    suspend fun clearFavorites()
}
