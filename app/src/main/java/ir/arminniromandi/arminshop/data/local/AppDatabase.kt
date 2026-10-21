package ir.arminniromandi.arminshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.arminniromandi.arminshop.data.local.dao.AddressDao
import ir.arminniromandi.arminshop.data.local.dao.CartDao
import ir.arminniromandi.arminshop.data.local.dao.NotificationDao
import ir.arminniromandi.arminshop.data.local.dao.OrderDao
import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.local.entity.UserAddressEntity

@Database(
    entities = [
        ProductEntity::class,
        ProductCommentEntity::class,
        CartItemEntity::class,
        UserAddressEntity::class,
        OrderEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
    abstract fun notificationDao(): NotificationDao
}
