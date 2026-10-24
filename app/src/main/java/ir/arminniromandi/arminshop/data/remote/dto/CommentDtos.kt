package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("product_id") val productId: Int?,
    @SerializedName("content") val content: String,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("user") val user: UserDto?,
    @SerializedName("created_at") val createdAt: String?
)

data class CommentsResponse(
    @SerializedName("comments") val comments: List<CommentDto>?
)

data class AddCommentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("rating") val rating: Int
)

data class AddCommentResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("comment") val comment: CommentDto?
)
