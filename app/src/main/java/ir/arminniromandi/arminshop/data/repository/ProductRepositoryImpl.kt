package ir.arminniromandi.arminshop.data.repository

import android.util.Log
import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.mapper.calculateFinalPrice
import ir.arminniromandi.arminshop.data.mapper.parseProductCategory
import ir.arminniromandi.arminshop.data.mapper.toDomain
import ir.arminniromandi.arminshop.data.mapper.toEntity
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.data.remote.ShopApiService
import ir.arminniromandi.arminshop.data.remote.dto.AddCommentRequest
import ir.arminniromandi.arminshop.data.remote.dto.UpdateRatingRequest
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.utils.Constance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val apiService: ShopApiService
) : ProductRepository {

    companion object {
        private const val TAG = "ProductRepositoryImpl"
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            checkAndSeedDatabase()
            refreshAll()
        }
    }

    private suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        if (productDao.getProductsCount() == 0) {
            val sampleProducts = MockProducts.sampleProducts
            val productEntities = sampleProducts.map { it.toEntity() }
            val commentEntities = sampleProducts.flatMap { product ->
                product.comments.map { it.toEntity(product.id) }
            }
            productDao.insertProducts(productEntities)
            if (commentEntities.isNotEmpty()) {
                productDao.insertComments(commentEntities)
            }
        }
    }

    override suspend fun refreshAll() = withContext(Dispatchers.IO) {
        syncSpecialOffers()
        syncAllCategories()
    }

    private suspend fun syncSpecialOffers() {
        try {
            val response = apiService.getSpecialOffers()
            if (response.isSuccessful) {
                val dtos = response.body()?.specialOffers ?: emptyList()
                if (dtos.isNotEmpty()) {
                    val entities = dtos.map { dto ->
                        dto.copy(isSpecialOffer = true).toEntity()
                    }
                    productDao.insertProducts(entities)
                    Log.d(TAG, "Successfully synced ${entities.size} special offers")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline or server unreachable for special offers: ${e.message}")
        }
    }

    private suspend fun syncAllCategories() {
        val categories = listOf("مردانه", "زنانه", "کفش", "بچه گانه")
        for (category in categories) {
            syncCategoryProducts(category)
        }
    }

    private suspend fun syncCategoryProducts(categoryIdentifier: String) {
        try {
            val response = apiService.getProductsByCategory(categoryIdentifier)
            if (response.isSuccessful) {
                val dtos = response.body()?.products ?: emptyList()
                if (dtos.isNotEmpty()) {
                    val entities = dtos.map { dto ->
                        dto.toEntity()
                    }
                    productDao.insertProducts(entities)
                    Log.d(TAG, "Successfully synced ${entities.size} products for category $categoryIdentifier")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline or server unreachable for category $categoryIdentifier: ${e.message}")
        }
    }

    private suspend fun syncProductDetails(productId: Int) {
        try {
            val response = apiService.getProductDetails(productId)
            if (response.isSuccessful) {
                val dto = response.body()?.product
                if (dto != null) {
                    val entity = dto.toEntity()
                    productDao.insertProduct(entity)

                    // Also sync comments if present in product details or comments endpoint
                    val commentsResponse = apiService.getComments(productId)
                    if (commentsResponse.isSuccessful) {
                        val commentDtos = commentsResponse.body()?.comments ?: emptyList()
                        if (commentDtos.isNotEmpty()) {
                            val commentEntities = commentDtos.map { it.toEntity(productId) }
                            productDao.insertComments(commentEntities)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline or server unreachable for product $productId: ${e.message}")
        }
    }

    override fun getProducts(): Flow<List<Product>> {
        return flow {
            checkAndSeedDatabase()
            repositoryScope.launch { refreshAll() }
            emitAll(
                productDao.getProductsWithComments().map { list ->
                    if (list.isEmpty()) {
                        MockProducts.sampleProducts
                    } else {
                        list.map { it.toDomain() }
                    }
                }
            )
        }
    }

    override fun getSpecialOffers(): Flow<List<Product>> {
        return flow {
            checkAndSeedDatabase()
            repositoryScope.launch { syncSpecialOffers() }
            emitAll(
                productDao.getSpecialOffersWithComments().map { list ->
                    if (list.isNotEmpty()) {
                        list.map { it.toDomain() }
                    } else {
                        // Fallback from cache to any discounted products or sample
                        val all = productDao.getProductsWithComments().firstOrNull() ?: emptyList()
                        val discounted = all.filter { it.product.discountPrec > 0 }
                        if (discounted.isNotEmpty()) {
                            discounted.map { it.toDomain() }
                        } else {
                            MockProducts.sampleProducts.filter { it.discountPrec > 0 }
                        }
                    }
                }
            )
        }
    }

    override fun getProductById(productId: Int): Flow<Product?> {
        return flow {
            checkAndSeedDatabase()
            repositoryScope.launch { syncProductDetails(productId) }
            emitAll(
                productDao.getProductWithCommentsById(productId).map { productWithComments ->
                    productWithComments?.toDomain()
                        ?: MockProducts.sampleProducts.find { p -> p.id == productId }
                }
            )
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        val trimmedQuery = query.trim()
        return flow {
            checkAndSeedDatabase()
            emitAll(
                productDao.searchProductsWithComments(trimmedQuery).map { list ->
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
            )
        }
    }

    override fun getProductsByCategory(categoryId: String, query: String): Flow<List<Product>> {
        val categoryItem = Constance.categories.find { it.id == categoryId || it.name == categoryId }
        val categoryTitle = categoryItem?.name ?: categoryId
        val categoryEnum = parseProductCategory(categoryTitle)
        val categoryEnumName = categoryEnum.name

        val trimmedQuery = query.trim()
        return flow {
            checkAndSeedDatabase()
            repositoryScope.launch { syncCategoryProducts(categoryTitle) }
            emitAll(
                productDao.getProductsByCategoryAndQuery(categoryEnumName, categoryTitle, trimmedQuery).map { list ->
                    val dbResults = list.map { it.toDomain() }
                    if (dbResults.isEmpty()) {
                        MockProducts.sampleProducts.filter { product ->
                            val matchesCategory = product.category == categoryEnum ||
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
            )
        }
    }

        override suspend fun addComment(productId: Int, comment: ProductComment, isReply: Boolean): Unit = withContext(Dispatchers.IO) {
        checkAndSeedDatabase()

        // 1. Enforce business rule: review text MUST not be blank
        val trimmedText = comment.text.trim()
        require(trimmedText.isNotBlank()) {
            "متن نظر نمی‌تواند خالی باشد. امتیاز به تنهایی قابل ارسال نیست."
        }

        // 2. Fetch current product rating & count from local DB
        val existingProductWithComments = productDao.getProductWithCommentsById(productId).firstOrNull()
        val currentRating = existingProductWithComments?.product?.rating ?: 5.0f
        val currentReviewsCount = existingProductWithComments?.comments?.size ?: 0

        // 3. Client-side recalculation of new average rating
        // If it is a reply or user rating is 0, do not modify product rating or count
        val shouldUpdateRating = !isReply && comment.rating > 0
        val newReviewsCount = if (shouldUpdateRating) currentReviewsCount + 1 else currentReviewsCount
        val newAverageRating = if (shouldUpdateRating) {
            val roundedRating = ((currentRating + comment.rating) / 2.0f * 10).roundToInt() / 10f
            roundedRating.coerceIn(1.0f, 5.0f)
        } else {
            currentRating
        }

        // 4. Try sending to server if online
        try {
            // Send comment first
            val commentResponse = apiService.addComment(
                productId = productId,
                request = AddCommentRequest(
                    content = trimmedText,
                    rating = if (shouldUpdateRating) comment.rating.toInt().coerceIn(1, 5) else 5
                )
            )
            Log.d(TAG, "Server comment response: ${commentResponse.code()} ${commentResponse.message()}")

            // Send updated calculated rating to server only if rating was updated
            if (shouldUpdateRating) {
                val ratingResponse = apiService.updateRating(
                    id = productId,
                    request = UpdateRatingRequest(rating = newAverageRating)
                )
                Log.d(TAG, "Server rating response: ${ratingResponse.code()} ${ratingResponse.message()}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to submit comment/rating to server (offline mode): ${e.message}")
        }

        // 5. Update local Room database so offline-first works instantly
        val commentEntity = comment.toEntity(productId)
        productDao.insertComment(commentEntity)

        if (shouldUpdateRating) {
            productDao.updateProductRatingAndReviewCount(
                productId = productId,
                newRating = newAverageRating,
                newReviewsCount = newReviewsCount
            )
        }
    }
}