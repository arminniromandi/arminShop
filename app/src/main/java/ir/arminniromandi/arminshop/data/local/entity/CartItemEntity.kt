package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val name: String,
    val imageRes: Int,
    val price: Long,
    val originalPrice: Long = price,
    val quantity: Int
)
