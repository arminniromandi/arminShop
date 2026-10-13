package ir.arminniromandi.arminshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, ProductCommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
