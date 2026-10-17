package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Transaction
    @Query("SELECT * FROM products")
    fun getProductsWithComments(): Flow<List<ProductWithComments>>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductWithCommentsById(productId: Int): Flow<ProductWithComments?>

    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ")
    fun searchProductsWithComments(query: String): Flow<List<ProductWithComments>>

    @Transaction
    @Query("SELECT * FROM products WHERE (category = :categoryEnumName OR category = :categoryTitle) AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')")
    fun getProductsByCategoryAndQuery(categoryEnumName: String, categoryTitle: String, query: String): Flow<List<ProductWithComments>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<ProductEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(comments: List<ProductCommentEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: ProductCommentEntity): Long

    @Query("SELECT COUNT(*) FROM products")
    fun getProductsCount(): Int

    @Query("UPDATE products SET rating = :newRating, reviewsCount = :newReviewsCount WHERE id = :productId")
    fun updateProductRatingAndReviewCount(productId: Int, newRating: Float, newReviewsCount: Int): Int
}
