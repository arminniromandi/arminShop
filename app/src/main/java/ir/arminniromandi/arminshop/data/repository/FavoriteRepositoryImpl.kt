package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.FavoriteDao
import ir.arminniromandi.arminshop.data.local.entity.FavoriteEntity
import ir.arminniromandi.arminshop.data.mapper.toDomain
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return favoriteDao.getFavoriteProducts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun isFavorite(productId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(productId)
    }

    override fun getFavoriteProductIds(): Flow<List<Int>> {
        return favoriteDao.getFavoriteProductIds()
    }

    override suspend fun addToFavorites(productId: Int): Unit = withContext(Dispatchers.IO) {
        favoriteDao.insertFavorite(FavoriteEntity(productId = productId))
    }

    override suspend fun removeFromFavorites(productId: Int): Unit = withContext(Dispatchers.IO) {
        favoriteDao.deleteFavorite(productId)
    }

    override suspend fun toggleFavorite(productId: Int): Unit = withContext(Dispatchers.IO) {
        if (favoriteDao.isFavoriteSync(productId)) {
            favoriteDao.deleteFavorite(productId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(productId = productId))
        }
    }

    override suspend fun clearFavorites(): Unit = withContext(Dispatchers.IO) {
        favoriteDao.clearFavorites()
    }
}
