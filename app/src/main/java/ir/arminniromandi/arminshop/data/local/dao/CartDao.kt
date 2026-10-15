package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getTotalItemCount(): Flow<Int?>

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    fun getCartItemById(productId: Int): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(cartItem: CartItemEntity): Long

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    fun updateQuantity(productId: Int, quantity: Int): Int

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    fun deleteCartItemByProductId(productId: Int): Int

    @Query("DELETE FROM cart_items")
    fun clearCart(): Int
}
