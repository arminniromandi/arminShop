package ir.arminniromandi.arminshop.data.remote

import ir.arminniromandi.arminshop.data.remote.dto.AddCommentRequest
import ir.arminniromandi.arminshop.data.remote.dto.AddCommentResponse
import ir.arminniromandi.arminshop.data.remote.dto.AddToCartRequest
import ir.arminniromandi.arminshop.data.remote.dto.AuthResponse
import ir.arminniromandi.arminshop.data.remote.dto.CartItemResponse
import ir.arminniromandi.arminshop.data.remote.dto.CartResponse
import ir.arminniromandi.arminshop.data.remote.dto.CategoryListResponse
import ir.arminniromandi.arminshop.data.remote.dto.CategoryProductsResponse
import ir.arminniromandi.arminshop.data.remote.dto.CommentsResponse
import ir.arminniromandi.arminshop.data.remote.dto.MessageResponse
import ir.arminniromandi.arminshop.data.remote.dto.ProductDetailsResponse
import ir.arminniromandi.arminshop.data.remote.dto.RatingResponse
import ir.arminniromandi.arminshop.data.remote.dto.SendOtpRequest
import ir.arminniromandi.arminshop.data.remote.dto.SpecialOffersResponse
import ir.arminniromandi.arminshop.data.remote.dto.UpdateRatingRequest
import ir.arminniromandi.arminshop.data.remote.dto.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopApiService {

    // Auth
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<MessageResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    // Categories & Products
    @GET("categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @GET("categories/{identifier}/products")
    suspend fun getProductsByCategory(@Path("identifier") identifier: String): Response<CategoryProductsResponse>

    @GET("products/special-offers")
    suspend fun getSpecialOffers(): Response<SpecialOffersResponse>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): Response<ProductDetailsResponse>

    @POST("products/{id}/rating")
    suspend fun updateRating(
        @Path("id") id: Int,
        @Body request: UpdateRatingRequest
    ): Response<RatingResponse>

    // Comments
    @GET("products/{id}/comments")
    suspend fun getComments(@Path("id") productId: Int): Response<CommentsResponse>

    @POST("products/{id}/comments")
    suspend fun addComment(
        @Path("id") productId: Int,
        @Body request: AddCommentRequest
    ): Response<AddCommentResponse>

    // Cart
    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("cart/items")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): Response<CartItemResponse>

    @DELETE("cart/items/{id}")
    suspend fun removeFromCart(
        @Path("id") itemId: Int
    ): Response<MessageResponse>
}
