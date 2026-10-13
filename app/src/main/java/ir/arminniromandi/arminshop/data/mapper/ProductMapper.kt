package ir.arminniromandi.arminshop.data.mapper

import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductWithComments
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.model.ProductFeatures

fun ProductWithComments.toDomain(): Product {
    val category = try {
        ProductCategory.valueOf(product.category)
    } catch (_: Exception) {
        ProductCategory.MEN
    }

    return Product(
        id = product.id,
        imageRes = product.imageRes,
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
        reviewsCount = product.reviewsCount,
        comments = comments.map { it.toDomain() },
        category = category
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
        name = name,
        description = description,
        weight = features.weight,
        color = features.color,
        mainPrice = mainPrice,
        finalPrice = finalPrice,
        discountPrec = discountPrec,
        rating = rating,
        reviewsCount = reviewsCount,
        category = category.name
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
