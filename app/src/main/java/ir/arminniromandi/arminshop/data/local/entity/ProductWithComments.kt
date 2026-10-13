package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithComments(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val comments: List<ProductCommentEntity>
)
