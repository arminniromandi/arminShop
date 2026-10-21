package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.arminniromandi.arminshop.data.local.entity.FavoriteEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Transaction
    @Query("SELECT products.* FROM products INNER JOIN favorites ON products.id = favorites.productId ORDER BY favorites.addedAt DESC")
    fun getFavoriteProducts(): Flow<List<ProductWithComments>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)")
    fun isFavorite(productId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)")
    suspend fun isFavoriteSync(productId: Int): Boolean

    @Query("SELECT productId FROM favorites ORDER BY addedAt DESC")
    fun getFavoriteProductIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun deleteFavorite(productId: Int): Int

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites(): Int
}
