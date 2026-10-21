package ir.arminniromandi.arminshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotifications(notifications: List<NotificationEntity>): List<Long>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    fun markAsRead(id: Long): Int

    @Query("UPDATE notifications SET isRead = :isRead WHERE id = :id")
    fun updateReadStatus(id: Long, isRead: Boolean): Int

    @Query("SELECT COUNT(*) FROM notifications")
    fun getNotificationsCount(): Int
}
