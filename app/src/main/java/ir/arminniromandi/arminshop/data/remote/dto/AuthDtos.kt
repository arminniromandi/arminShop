package ir.arminniromandi.arminshop.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String
)

data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: UserDto?,
    @SerializedName("message") val message: String?
)

data class MessageResponse(
    @SerializedName("message") val message: String?
)
