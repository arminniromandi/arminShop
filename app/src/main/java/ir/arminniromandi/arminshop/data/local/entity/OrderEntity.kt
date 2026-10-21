package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderCode: String,
    val orderDate: String,
    val productId: Int,
    val productName: String,
    val productImageRes: Int,
    val price: Long,
    val quantity: Int = 1,
    val isPaymentSuccess: Boolean = true,
    val hasCommented: Boolean = false,
    val commentRating: Float? = null,
    val commentText: String? = null
)
