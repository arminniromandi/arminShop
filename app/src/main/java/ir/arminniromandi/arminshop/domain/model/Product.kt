package ir.arminniromandi.arminshop.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import ir.arminniromandi.arminshop.R

/**
 * Categories available in the store
 */
enum class ProductCategory(val title: String) {
    MEN("مردانه"),
    WOMEN("زنانه"),
    SHOES("کفش"),
    KIDS("بچه گانه")
}

/**
 * Specific attributes / features of a product
 */
@Immutable
data class ProductFeatures(
    val weight: String = "",
    val color: String = ""
)

/**
 * User review/comment on a product
 */
@Immutable
data class ProductComment(
    val id: Int,
    val userName: String,
    val rating: Float, // 1.0 to 5.0
    val text: String,
    val date: String
)

/**
 * Product domain model
 */
@Immutable
data class Product(
    val id: Int,
    @param:DrawableRes val imageRes: Int = R.drawable.man_category,
    val imageUrl: String? = null,
    val name: String,
    val description: String,
    val features: ProductFeatures = ProductFeatures(),
    val mainPrice: Long,
    val finalPrice: Long,
    val discountPrec: Int,
    val rating: Float, // Out of 5.0 (e.g. 4.6)
    val reviewsCount: Int = 0, // Total review count
    val comments: List<ProductComment> = emptyList(), // Product comments/reviews
    val category: ProductCategory,
    val isSpecialOffer: Boolean = false
)
