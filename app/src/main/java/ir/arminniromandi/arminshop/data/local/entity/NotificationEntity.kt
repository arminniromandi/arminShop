package ir.arminniromandi.arminshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val sender: String = "فروشگاه",
    val date: String,
    val message: String,
    val isRead: Boolean = false,
    val orderCode: String? = null
)
