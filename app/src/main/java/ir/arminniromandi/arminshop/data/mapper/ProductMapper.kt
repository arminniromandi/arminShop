package ir.arminniromandi.arminshop.data.mapper

import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductWithComments
import ir.arminniromandi.arminshop.data.remote.dto.CommentDto
import ir.arminniromandi.arminshop.data.remote.dto.ProductDto
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.model.ProductFeatures

fun parseProductCategory(rawCategory: String?): ProductCategory {
    if (rawCategory.isNullOrBlank()) return ProductCategory.MEN
    val normalized = rawCategory.trim().replace(" ", "")
    return when {
        normalized.equals("مردانه", ignoreCase = true) || normalized.equals("MEN", ignoreCase = true) -> ProductCategory.MEN
        normalized.equals("زنانه", ignoreCase = true) || normalized.equals("WOMEN", ignoreCase = true) -> ProductCategory.WOMEN
        normalized.equals("کفش", ignoreCase = true) || normalized.equals("SHOES", ignoreCase = true) -> ProductCategory.SHOES
        normalized.equals("بچهگانه", ignoreCase = true) || normalized.equals("بچه گانه", ignoreCase = true) || normalized.equals("KIDS", ignoreCase = true) -> ProductCategory.KIDS
        else -> try {
            ProductCategory.valueOf(rawCategory.trim().uppercase())
        } catch (_: Exception) {
            ProductCategory.MEN
        }
    }
}

fun getDefaultDrawableForCategory(category: ProductCategory): Int {
    return when (category) {
        ProductCategory.MEN -> R.drawable.man_category
        ProductCategory.WOMEN -> R.drawable.woman_category
        ProductCategory.SHOES -> R.drawable.shoes_category
        ProductCategory.KIDS -> R.drawable.baby_category
    }
}

/**
 * Calculates discounted price based on base price and discount percentage
 */
fun calculateFinalPrice(basePrice: Long, discountPercent: Int): Long {
    if (discountPercent <= 0) return basePrice
    val discountAmount = (basePrice * discountPercent) / 100
    return basePrice - discountAmount
}

fun ProductWithComments.toDomain(): Product {
    val cat = parseProductCategory(product.category)

    return Product(
        id = product.id,
        imageRes = if (product.imageRes != 0) product.imageRes else getDefaultDrawableForCategory(cat),
        imageUrl = product.imageUrl,
        name = product.name,
        description = product.description,
        features = ProductFeatures(
            weight = product.weight,
            color = product.color
        ),
        mainPrice = product.mainPrice,
        finalPrice = product.finalPrice,
        discountPrec = product.discountPrec,
        rating = product.rating,
        reviewsCount = if (product.reviewsCount > 0) product.reviewsCount else comments.size,
        comments = comments.map { it.toDomain() },
        category = cat,
        isSpecialOffer = product.isSpecialOffer
    )
}

fun ProductCommentEntity.toDomain(): ProductComment {
    return ProductComment(
        id = id,
        userName = userName,
        rating = rating,
        text = text,
        date = date
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        imageRes = imageRes,
        imageUrl = imageUrl,
        name = name,
        description = description,
        weight = features.weight,
        color = features.color,
        mainPrice = mainPrice,
        finalPrice = finalPrice,
        discountPrec = discountPrec,
        rating = rating,
        reviewsCount = reviewsCount,
        category = category.name,
        isSpecialOffer = isSpecialOffer
    )
}

fun ProductComment.toEntity(productId: Int): ProductCommentEntity {
    return ProductCommentEntity(
        id = id,
        productId = productId,
        userName = userName,
        rating = rating,
        text = text,
        date = date
    )
}

/**
 * Convert Remote DTO to Room Entity, computing client-side final price
 */
fun ProductDto.toEntity(fallbackImageRes: Int = 0): ProductEntity {
    val cat = parseProductCategory(category)
    val discount = discountPercent ?: 0
    val computedFinalPrice = calculateFinalPrice(price, discount)
    val defaultRes = if (fallbackImageRes != 0) fallbackImageRes else getDefaultDrawableForCategory(cat)

    return ProductEntity(
        id = id,
        imageRes = defaultRes,
        imageUrl = imageUrl,
        name = name,
        description = description ?: "",
        weight = weight ?: "سبک و راحت",
        color = color ?: "تنوع رنگی",
        mainPrice = price,
        finalPrice = computedFinalPrice,
        discountPrec = discount,
        rating = rating ?: 5.0f,
        reviewsCount = comments?.size ?: 0,
        category = cat.name,
        isSpecialOffer = isSpecialOffer == true || discount > 0
    )
}

fun CommentDto.toEntity(defaultProductId: Int): ProductCommentEntity {
    return ProductCommentEntity(
        id = id,
        productId = productId ?: defaultProductId,
        userName = user?.email?.substringBefore("@") ?: "کاربر",
        rating = rating ?: 5f,
        text = content,
        date = createdAt?.take(10) ?: "همین حالا"
    )
}
