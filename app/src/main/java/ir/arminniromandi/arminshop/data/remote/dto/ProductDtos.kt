package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("category") val category: String?,
    @SerializedName("name") val name: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Long,
    @SerializedName("discount_percent") val discountPercent: Int?,
    @SerializedName("is_special_offer") val isSpecialOffer: Boolean?,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("weight") val weight: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("comments") val comments: List<CommentDto>?
)

data class CategoryProductsResponse(
    @SerializedName("category") val category: String?,
    @SerializedName("products") val products: List<ProductDto>?
)

data class SpecialOffersResponse(
    @SerializedName("special_offers") val specialOffers: List<ProductDto>?
)

data class ProductDetailsResponse(
    @SerializedName("product") val product: ProductDto?
)
