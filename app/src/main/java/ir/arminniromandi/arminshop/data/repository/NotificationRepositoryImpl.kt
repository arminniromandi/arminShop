package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.NotificationDao
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import ir.arminniromandi.arminshop.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun addNotification(notification: NotificationEntity): Long = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
    }

    override suspend fun addNotifications(notifications: List<NotificationEntity>): List<Long> = withContext(Dispatchers.IO) {
        notificationDao.insertNotifications(notifications)
    }

    override suspend fun markAsRead(id: Long): Boolean = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id) > 0
    }

    override suspend fun updateReadStatus(id: Long, isRead: Boolean): Boolean = withContext(Dispatchers.IO) {
        notificationDao.updateReadStatus(id, isRead) > 0
    }
}
