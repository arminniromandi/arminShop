package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<OrderEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET hasCommented = 1, commentRating = :rating, commentText = :comment WHERE id = :orderId")
    fun updateOrderComment(orderId: Long, rating: Float, comment: String): Int

    @Query("SELECT COUNT(*) FROM orders")
    fun getOrdersCount(): Int
}
