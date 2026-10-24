package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("discount_percent") val discountPercent: Int?,
    @SerializedName("image_url") val imageUrl: String?
)

data class CartItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: CartProductDto?
)

data class CartResponse(
    @SerializedName("count") val count: Int?,
    @SerializedName("total_price") val totalPrice: Long?,
    @SerializedName("items") val items: List<CartItemDto>?
)

data class AddToCartRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int
)

data class CartItemResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("cart_item") val cartItem: CartItemDto?
)
