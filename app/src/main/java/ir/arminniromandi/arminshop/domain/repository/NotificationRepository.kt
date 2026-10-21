package ir.arminniromandi.arminshop.domain.repository

import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    suspend fun addNotification(notification: NotificationEntity): Long
    suspend fun addNotifications(notifications: List<NotificationEntity>): List<Long>
    suspend fun markAsRead(id: Long): Boolean
    suspend fun updateReadStatus(id: Long, isRead: Boolean): Boolean
}
