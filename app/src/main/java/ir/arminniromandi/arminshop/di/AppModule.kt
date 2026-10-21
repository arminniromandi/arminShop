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
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.data.local.AppDatabase
import ir.arminniromandi.arminshop.data.local.UserPreferencesDataSource
import ir.arminniromandi.arminshop.data.local.dao.AddressDao
import ir.arminniromandi.arminshop.data.local.dao.CartDao
import ir.arminniromandi.arminshop.data.local.dao.NotificationDao
import ir.arminniromandi.arminshop.data.local.dao.OrderDao
import ir.arminniromandi.arminshop.data.local.dao.ProductDao
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductCommentEntity
import ir.arminniromandi.arminshop.data.local.entity.ProductEntity
import ir.arminniromandi.arminshop.data.local.entity.UserAddressEntity
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.data.repository.AddressRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.CartRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.NotificationRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.OrderRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.ProductRepositoryImpl
import ir.arminniromandi.arminshop.data.repository.UserRepositoryImpl
import ir.arminniromandi.arminshop.domain.repository.AddressRepository
import ir.arminniromandi.arminshop.domain.repository.CartRepository
import ir.arminniromandi.arminshop.domain.repository.NotificationRepository
import ir.arminniromandi.arminshop.domain.repository.OrderRepository
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
        databaseProvider: Provider<AppDatabase>
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
                    val appDb = databaseProvider.get()
                    seedInitialData(appDb)
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val appDb = databaseProvider.get()
                    seedInitialDataIfNeeded(appDb)
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

    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideOrderRepository(orderDao: OrderDao): OrderRepository {
        return OrderRepositoryImpl(orderDao)
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepositoryImpl(notificationDao)
    }

    private fun seedInitialData(database: AppDatabase) {
        seedMockProducts(database.productDao())
        seedInitialAddresses(database.addressDao())
        seedInitialOrders(database.orderDao())
        seedInitialNotifications(database.notificationDao())
    }

    private fun seedInitialDataIfNeeded(database: AppDatabase) {
        if (database.productDao().getProductsCount() == 0) {
            seedMockProducts(database.productDao())
        }
        seedInitialAddresses(database.addressDao())
        if (database.orderDao().getOrdersCount() == 0) {
            seedInitialOrders(database.orderDao())
        }
        if (database.notificationDao().getNotificationsCount() == 0) {
            seedInitialNotifications(database.notificationDao())
        }
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

    private fun seedInitialAddresses(dao: AddressDao) {
        val existing = dao.getAddressById(1L)
        if (existing == null) {
            dao.insertAddress(
                UserAddressEntity(
                    id = 1L,
                    recipientName = "محمد عزیزی",
                    fullAddress = "بیرجند-غفاری ۱۶-پلاک ۲۵",
                    postalCode = "۹۸۷۴۳۶۵۳۷۴",
                    phoneNumber = "۰۹۱۱۱۱۱۱۱۱۱",
                    isDefault = true
                )
            )
            dao.insertAddress(
                UserAddressEntity(
                    id = 2L,
                    recipientName = "الهه لطفی",
                    fullAddress = "بیرجند- توحید ۱۶-پلاک ۲۵",
                    postalCode = "۹۸۷۴۳۶۵۳۷۴",
                    phoneNumber = "۰۹۱۱۱۱۱۱۱۱۱",
                    isDefault = false
                )
            )
        }
    }

    private fun seedInitialOrders(dao: OrderDao) {
        val initialOrders = listOf(
            OrderEntity(
                orderCode = "۱۲۳۶۴",
                orderDate = "۲۶ مهر ۱۴۰۲",
                productId = 1,
                productName = "ست سویشرت و شلوار مردانه",
                productImageRes = R.drawable.malecloth,
                price = 1550000L,
                quantity = 1,
                isPaymentSuccess = true,
                hasCommented = false
            ),
            OrderEntity(
                orderCode = "۱۲۳۶۴",
                orderDate = "۲۶ مهر ۱۴۰۲",
                productId = 3,
                productName = "ست سویشرت و شلوار مردانه ست سویشرت و شلوار مردانه",
                productImageRes = R.drawable.malecloth2,
                price = 1550000L,
                quantity = 1,
                isPaymentSuccess = true,
                hasCommented = true,
                commentRating = 3f,
                commentText = null
            ),
            OrderEntity(
                orderCode = "۱۲۳۶۴",
                orderDate = "۲۶ مهر ۱۴۰۲",
                productId = 2,
                productName = "ست سویشرت و شلوار مردانه",
                productImageRes = R.drawable.shirtmale,
                price = 1550000L,
                quantity = 1,
                isPaymentSuccess = false,
                hasCommented = true,
                commentRating = 4.5f,
                commentText = "عالی بود، از نظر جنس و کیفیت خیلی خوب بود و دقیقا شبیه به عکس بود"
            ),
            OrderEntity(
                orderCode = "۱۲۳۶۴",
                orderDate = "۲۶ مهر ۱۴۰۲",
                productId = 7,
                productName = "پیراهن کتان آبی",
                productImageRes = R.drawable.bluemancloth,
                price = 1550000L,
                quantity = 1,
                isPaymentSuccess = true,
                hasCommented = true,
                commentRating = 4.5f,
                commentText = "عالی بود، از نظر جنس و کیفیت بسیار خوب بود و دقیقا شبیه عکس بود"
            )
        )
        dao.insertOrders(initialOrders)
    }

    private fun seedInitialNotifications(dao: NotificationDao) {
        val initialNotifications = listOf(
            NotificationEntity(
                title = "ثبت موفق سفارش",
                sender = "فروشگاه",
                date = "۲۶ مهر ۱۴۰۲",
                message = "متن اعلان در اینجا نوشته می شود. سفارش شما با کد پیگیری ۱۲۳۶۴ ثبت گردید. این اعلان شامل اطلاعات تحویل کالا است.",
                isRead = false,
                orderCode = "۱۲۳۶۴"
            ),
            NotificationEntity(
                title = "ثبت موفق سفارش",
                sender = "فروشگاه",
                date = "۲۶ مهر ۱۴۰۲",
                message = "سفارش شما با کد پیگیری ۱۲۳۶۰ با موفقیت در فروشگاه ثبت شد و به زودی ارسال می‌گردد.",
                isRead = true,
                orderCode = "۱۲۳۶۰"
            )
        )
        dao.insertNotifications(initialNotifications)
    }
}
