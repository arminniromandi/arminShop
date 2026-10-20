package ir.arminniromandi.arminshop.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.arminniromandi.arminshop.data.local.AppDatabase
import ir.arminniromandi.arminshop.data.local.UserPreferencesDataSource
import ir.arminniromandi.arminshop.data.local.dao.AddressDao
import ir.arminniromandi.arminshop.data.local.dao.CartDao
import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.data.repository.AddressRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.CartRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.ProductRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.UserRepositoryImpl
import ir.arminniromandi.arminshop.domain.repository.AddressRepository
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATABASE_NAME = "armin_shop_db"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(userPreferencesDataSource: UserPreferencesDataSource): UserRepository {
        return UserRepositoryImpl(userPreferencesDataSource)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        productDaoProvider: Provider<ProductDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = productDaoProvider.get()
                    seedMockProducts(dao)
                }
            }
        }).build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepositoryImpl(productDao)
    }

    @Provides
    @Singleton
    fun provideCartRepository(cartDao: CartDao): CartRepository {
        return CartRepositoryImpl(cartDao)
    }

    @Provides
    fun provideAddressDao(database: AppDatabase): AddressDao {
        return database.addressDao()
    }

    @Provides
    @Singleton
    fun provideAddressRepository(addressDao: AddressDao): AddressRepository {
        return AddressRepositoryImpl(addressDao)
    }

    private fun seedMockProducts(dao: ProductDao) {
        val sampleProducts = MockProducts.sampleProducts

        val productEntities = sampleProducts.map { product ->
            ProductEntity(
                id = product.id,
                imageRes = product.imageRes,
                name = product.name,
                description = product.description,
                weight = product.features.weight,
                color = product.features.color,
                mainPrice = product.mainPrice,
                finalPrice = product.finalPrice,
                discountPrec = product.discountPrec,
                rating = product.rating,
                reviewsCount = product.reviewsCount,
                category = product.category.name
            )
        }

        val commentEntities = sampleProducts.flatMap { product ->
            product.comments.map { comment ->
                ProductCommentEntity(
                    id = comment.id,
                    productId = product.id,
                    userName = comment.userName,
                    rating = comment.rating,
                    text = comment.text,
                    date = comment.date
                )
            }
        }

        dao.insertProducts(productEntities)
        dao.insertComments(commentEntities)
    }
}
