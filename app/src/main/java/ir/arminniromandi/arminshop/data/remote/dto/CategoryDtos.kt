package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryItemDto(
    @SerializedName("category") val category: String
)

data class CategoryListResponse(
    @SerializedName("categories") val categories: List<CategoryItemDto>?
)
