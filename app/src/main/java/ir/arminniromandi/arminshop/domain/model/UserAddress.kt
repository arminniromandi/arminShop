package ir.arminniromandi.arminshop.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserAddress(
    val id: Long = 0,
    val recipientName: String,
    val fullAddress: String,
    val postalCode: String,
    val phoneNumber: String,
    val isDefault: Boolean = false
)
