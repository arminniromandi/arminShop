package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateRatingRequest(
    @SerializedName("rating") val rating: Float
)

data class RatingResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("product_id") val productId: Int?,
    @SerializedName("rating") val rating: Float?
)
