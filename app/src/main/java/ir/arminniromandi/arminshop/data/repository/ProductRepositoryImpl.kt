package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.mapper.toDomain
import ir.arminniromandi.arminshop.data.mapper.toEntity
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.utils.Constance
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
            if (list.isEmpty()) {
                MockProducts.sampleProducts
            } else {
                list.map { it.toDomain() }
            }
        }
    }

    override fun getProductById(productId: Int): Flow<Product?> {
        return productDao.getProductWithCommentsById(productId).map {
            it?.toDomain() ?: MockProducts.sampleProducts.find { p -> p.id == productId }
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        val trimmedQuery = query.trim()
        return productDao.searchProductsWithComments(trimmedQuery).map { list ->
            val dbResults = list.map { it.toDomain() }
            if (dbResults.isEmpty()) {
                MockProducts.sampleProducts.filter { product ->
                    product.name.contains(trimmedQuery, ignoreCase = true) ||
                            product.description.contains(trimmedQuery, ignoreCase = true)
                }
            } else {
                dbResults
            }
        }
    }

    override fun getProductsByCategory(categoryId: String, query: String): Flow<List<Product>> {
        val categoryItem = Constance.categories.find { it.id == categoryId || it.name == categoryId }
        val categoryTitle = categoryItem?.name ?: categoryId
        val categoryEnumName = ProductCategory.entries.find {
            it.name.equals(categoryId, ignoreCase = true) || it.title == categoryTitle
        }?.name ?: categoryTitle

        val trimmedQuery = query.trim()
        return productDao.getProductsByCategoryAndQuery(categoryEnumName, categoryTitle, trimmedQuery).map { list ->
            val dbResults = list.map { it.toDomain() }
            if (dbResults.isEmpty()) {
                MockProducts.sampleProducts.filter { product ->
                    val matchesCategory = product.category.name.equals(categoryEnumName, ignoreCase = true) ||
                            product.category.title == categoryTitle
                    val matchesQuery = trimmedQuery.isBlank() ||
                            product.name.contains(trimmedQuery, ignoreCase = true) ||
                            product.description.contains(trimmedQuery, ignoreCase = true)
                    matchesCategory && matchesQuery
                }
            } else {
                dbResults
            }
        }
    }

    override suspend fun addComment(productId: Int, comment: ProductComment): Unit = withContext(Dispatchers.IO) {
        val existingDbProduct = productDao.getProductWithCommentsById(productId).firstOrNull()
        if (existingDbProduct == null) {
            val mockProduct = MockProducts.sampleProducts.find { it.id == productId }
            if (mockProduct != null) {
                productDao.insertProducts(listOf(mockProduct.toEntity()))
            }
        }

        // Insert new comment
        productDao.insertComment(comment.toEntity(productId))

        // Recalculate average rating and total review count from Room DB
        val updatedProductWithComments = productDao.getProductWithCommentsById(productId).firstOrNull()
        if (updatedProductWithComments != null) {
            val comments = updatedProductWithComments.comments
            val count = comments.size
            val avgRating = if (count > 0) {
                (comments.sumOf { it.rating.toDouble() } / count).toFloat()
            } else {
                0f
            }
            productDao.updateProductRatingAndReviewCount(
                productId = productId,
                newRating = avgRating,
                newReviewsCount = count
            )
        }
    }
}
