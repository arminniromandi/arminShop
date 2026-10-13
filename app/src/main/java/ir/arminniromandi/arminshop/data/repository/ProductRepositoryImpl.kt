package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.mapper.toDomain
import ir.arminniromandi.arminshop.data.mapper.toEntity
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getProductsWithComments().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductById(productId: Int): Flow<Product?> {
        return productDao.getProductWithCommentsById(productId).map {
            it?.toDomain()
        }
    }

    override suspend fun addComment(productId: Int, comment: ProductComment) = withContext(Dispatchers.IO) {
        val currentProduct = productDao.getProductWithCommentsById(productId).firstOrNull()?.toDomain()

        productDao.insertComment(comment.toEntity(productId))

        if (currentProduct != null) {
            val newCount = currentProduct.reviewsCount + 1
            val newRating = ((currentProduct.rating * currentProduct.reviewsCount) + comment.rating) / newCount
            productDao.updateProductRatingAndReviewCount(
                productId = productId,
                newRating = newRating,
                newReviewsCount = newCount
            )
        }
    }
}
