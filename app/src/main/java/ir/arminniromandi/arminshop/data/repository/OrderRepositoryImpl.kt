package ir.arminniromandi.arminshop.data.repository

import ir.arminniromandi.arminshop.data.local.dao.OrderDao
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }

    override suspend fun addOrders(orders: List<OrderEntity>): List<Long> = withContext(Dispatchers.IO) {
        orderDao.insertOrders(orders)
    }

    override suspend fun addOrder(order: OrderEntity): Long = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
    }

    override suspend fun updateOrderComment(orderId: Long, rating: Float, comment: String): Boolean = withContext(Dispatchers.IO) {
        orderDao.updateOrderComment(orderId, rating, comment) > 0
    }
}
