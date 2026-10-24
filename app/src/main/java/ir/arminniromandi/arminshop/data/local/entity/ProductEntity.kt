package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val imageRes: Int,
    val imageUrl: String? = null,
    val name: String,
    val description: String,
    val weight: String,
    val color: String,
    val mainPrice: Long,
    val finalPrice: Long,
    val discountPrec: Int,
    val rating: Float,
    val reviewsCount: Int,
    val category: String,
    val isSpecialOffer: Boolean = false
)
