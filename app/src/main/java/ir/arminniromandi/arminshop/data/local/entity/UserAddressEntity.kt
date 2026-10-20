package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_addresses")
data class UserAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientName: String,
    val fullAddress: String,
    val postalCode: String,
    val phoneNumber: String,
    val isDefault: Boolean = false
)
